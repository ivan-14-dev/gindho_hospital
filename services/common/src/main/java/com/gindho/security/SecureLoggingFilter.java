package com.gindho.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Filtre de logging sécurisé :
 * - Masque les données personnelles dans les logs
 * - Ne loggue pas les querystrings brutes
 * - Filtre les champs sensibles (token, password, secret)
 */
@Slf4j
@Component
public class SecureLoggingFilter implements Filter {

    private static final Pattern[] SENSITIVE_PATTERNS = {
        Pattern.compile("(?i)(password|secret|token|authorization)=[^&\\\\s]+", Pattern.MULTILINE),
        Pattern.compile("\\\\b(\\\\d{15,16})\\\\b"), // Cartes bancaires
        Pattern.compile("\\\\b(\\\\d{2}/\\\\d{2}/\\\\d{4})\\\\b") // Dates de naissance dans les logs
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;
        
        String uri = httpReq.getRequestURI();
        String method = httpReq.getMethod();
        
        // Log sécurisé : pas de querystring brute
        if (log.isDebugEnabled()) {
            log.debug("[{}] {} — IP: {}", method, uri, getClientIp(httpReq));
        }
        
        chain.doFilter(request, response);
        
        if (httpResp.getStatus() >= 500) {
            log.warn("[{}] {} → {} (erreur serveur)", method, uri, httpResp.getStatus());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Masque les données sensibles dans une chaîne.
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        String result = input;
        for (Pattern p : SENSITIVE_PATTERNS) {
            result = p.matcher(result).replaceAll("***");
        }
        return result;
    }
}
