package com.gindho.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.gindho.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuditLoggingAspect {

    private final AuditLogService auditLogService;

    public AuditLoggingAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object aroundRestControllers(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = currentRequest().orElse(null);

        Object result = pjp.proceed();

        if (request == null) {
            return result;
        }

        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/api/auth/")) {
            // Ne pas bloquer les endpoints AUTH si l'audit échoue.
            return result;
        }

        String method = request.getMethod();
        String resource = extractResource(request);
        String entiteId = extractEntityId(request);
        String actor = actor();

        String details = "method=" + method + ", uri=" + request.getRequestURI()
                + ", query=" + safeQuery(request)
                + ", actor=" + actor;

        String ip = request.getRemoteAddr();
        try {
            auditLogService.log(
                    mapAction(method),
                    resource,
                    entiteId,
                    details,
                    ip
            );
        } catch (Throwable auditEx) {
            // Ne jamais casser la requête principale à cause de l'audit
            System.err.println("AuditLog failed: " + auditEx.getClass().getName() + " - " + auditEx.getMessage());
        }

        return result;
    }

    private java.util.Optional<HttpServletRequest> currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes sra)) return java.util.Optional.empty();
        return java.util.Optional.ofNullable(sra.getRequest());
    }

    private String actor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? String.valueOf(auth.getName()) : "anonymous";
    }

    private String extractResource(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/api/")) return "UNKNOWN";

        String tail = uri.substring("/api/".length()); // e.g. "analyses/..."
        int slash = tail.indexOf('/');
        String first = slash >= 0 ? tail.substring(0, slash) : tail;

        first = (first == null || first.isBlank()) ? "UNKNOWN" : first.trim();
        // normaliser "-": "audit-logs" -> "audit_logs"
        return first.replace('-', '_').toUpperCase();
    }

    private String extractEntityId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/api/")) return null;

        String tail = uri.substring("/api/".length());
        String[] parts = tail.split("/");

        for (int i = parts.length - 1; i >= 0; i--) {
            String p = parts[i];
            if (p != null && p.matches("\\d+")) {
                return p;
            }
        }
        return null;
    }

    private String safeQuery(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isBlank()) return "none";

        // Anti log-spam + anti-données énormes
        int maxLen = 600;
        if (query.length() > maxLen) {
            query = query.substring(0, maxLen) + "...";
        }

        String[] parts = query.split("&");
        java.util.List<String> redacted = new java.util.ArrayList<>();

        for (String part : parts) {
            if (part == null || part.isBlank()) continue;

            String[] kv = part.split("=", 2);
            String key = kv.length > 0 ? kv[0] : "";
            String value = kv.length == 2 ? kv[1] : "";

            if (key == null) key = "";
            String keyUpper = key.toUpperCase(java.util.Locale.ROOT);

            boolean sensitive =
                    keyUpper.contains("TOKEN") ||
                    keyUpper.contains("PASSWORD") ||
                    keyUpper.contains("AUTHORIZATION") ||
                    keyUpper.contains("MOTIF") ||
                    keyUpper.contains("MOTTO") ||
                    keyUpper.contains("OBSERV") ||
                    keyUpper.contains("DIAGN") ||
                    keyUpper.contains("TRAITEM");

            if (sensitive) {
                redacted.add(key + "=***");
            } else {
                if (value != null && value.length() > 120) {
                    value = value.substring(0, 120) + "...";
                }
                redacted.add(key + "=" + (value == null ? "" : value));
            }
        }

        return String.join("&", redacted);
    }

    private String mapAction(String httpMethod) {
        if (httpMethod == null) return "READ";
        String m = httpMethod.toUpperCase(java.util.Locale.ROOT);
        return switch (m) {
            case "GET" -> "READ";
            case "POST" -> "WRITE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> "READ";
        };
    }
}
