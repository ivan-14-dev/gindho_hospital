package com.gindho.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gindho.model.RolePermission;
import com.gindho.model.User;
import com.gindho.repository.UserRepository;
import com.gindho.service.PatientAccessService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DynamicPermissionFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final PatientAccessService patientAccessService;

    public DynamicPermissionFilter(UserRepository userRepository, PatientAccessService patientAccessService) {
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Toujours laisser passer OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String path = request.getRequestURI();
        if (path == null || !path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bypass ciblé: les endpoints "patient/*" sont gérés par SecurityConfig (RBAC) + ownership (PatientAccessService)
        // Empêche un 403 dû au filtrage dynamique si le catalogue dynamique n'est pas aligné.
        if (path.startsWith("/api/revenus/patient/")
                || path.startsWith("/api/factures/patient/")
                || path.startsWith("/api/paiements/patient/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bypass statique (SecurityConfig) : ces endpoints sont déjà autorisés par matcher RBAC statique.
        // On évite que le filtrage dynamique force des permissions dynamiques non seedées.
        String method = request.getMethod() == null ? "" : request.getMethod().trim().toUpperCase(Locale.ROOT);

        // Bypass: endpoint "patient courant" utilisé par les portails pour résoudre le patientId
        // (évite blocage dynamique si permissions dynamiques PATIENTS:* ne sont pas seedées)
        if (path != null && path.startsWith("/api/patients/me") && "GET".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bypass portail patient: dossiers + prescriptions
        // Objectif: éviter un 403 "Permission requise" si le catalogue dynamique n'est pas seedé
        // (la source de vérité reste ensuite RBAC/ownership côté service).
        if (patientPortalBypass(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("/api/medecins".equals(path) && "GET".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        if ("/api/rendezvous".equals(path) && ("GET".equals(method) || "POST".equals(method))) {
            filterChain.doFilter(request, response);
            return;
        }

        // Exclusions publics (auth + swagger)
        if (path.startsWith("/api/auth/") || path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/api/admin/")) {
            // Admin backoffice reste contrôlé par SecurityConfig (hasAnyRole).
            // On évite ici d'ajouter une logique double qui casserait les droits existants.
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        Set<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Normalisation pour matcher robuste:
        // - trim
        // - '-' => '_'
        // - upper-case
        // (utile car côté DB/UI les permissions peuvent être en 'patients:read' ou 'patients:READ', etc.)
        Set<String> normalizedAuthorities = authorities.stream()
                .filter(a -> a != null)
                .map(String::trim)
                .filter(a -> !a.isEmpty())
                .map(a -> a.replace('-', '_').toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());

        // Bypass: certains endpoints sont déjà autorisés par le SecurityConfig statique
        // (donc DynamicPermissionFilter bloque inutilement si les permissions dynamiques ne sont pas seedées).
        // Contrat (PATIENT):
        // - GET /api/medecins
        // - GET /api/rendezvous, POST /api/rendezvous
        // - GET /api/patients/{id}/dossier-complet
        // - GET /api/prescriptions
        //
        // Objectif: éviter que le filtrage dynamique bloque le portail patient si les permissions dynamiques ne sont pas seedées
        // (le SecurityConfig + ownership côté service doivent rester la source de vérité).
        if (normalizedAuthorities.contains("ROLE_PATIENT")) {
            String patientMethod = request.getMethod() == null ? "" : request.getMethod().trim().toUpperCase(Locale.ROOT);

            if (patientMethod.equals("GET") && "/api/medecins".equals(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Patient a besoin de lire les disponibilités pour créer un RDV
            if (patientMethod.equals("GET") && "/api/disponibilites".equals(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (("/api/rendezvous".equals(path)) && (patientMethod.equals("GET") || patientMethod.equals("POST"))) {
                filterChain.doFilter(request, response);
                return;
            }

            if (patientMethod.equals("GET") && "/api/prescriptions".equals(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (patientMethod.equals("GET")
                    && path != null
                    && path.startsWith("/api/patients/")
                    && path.endsWith("/dossier-complet")) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (normalizedAuthorities.contains("ROLE_SUPER_ADMIN")) {
            filterChain.doFilter(request, response);
            return;
        }

        // HYBRIDE :
        // Si l'utilisateur ne possède aucune authority dynamique (uniquement ROLE_*),
        // alors on laisse SecurityConfig faire l'autorisation (évite de bloquer les seed users).
        //
        // IMPORTANT (sécurité) :
        // - /api/audit-logs n'a pas de matcher dédié dans SecurityConfig
        // - on doit donc EXIGER une permission dynamique pour éviter que tout user authentifié y accède
        boolean requiresDynamicPermission = path.startsWith("/api/audit-logs");
        boolean hasDynamicAuthorities = authorities.stream()
                .anyMatch(a -> a != null && !a.startsWith("ROLE_"));

        // Cas audit-logs:
        // - SecurityConfig n'a pas de matcher dédié pour /api/audit-logs
        // - donc si l'utilisateur n'a que des ROLE_* (ex: ROLE_ADMIN) et pas de permissions dynamiques,
        // on doit laisser passer (sinon on obtient un 403 malgré le rôle statique).
        if (!hasDynamicAuthorities && (!requiresDynamicPermission || normalizedAuthorities.contains("ROLE_ADMIN"))) {
            // Important: on laisse SecurityConfig (ROLE_* matchers) gérer.
            // Évite de bloquer de manière “double” certains endpoints qui devraient passer sans permission dynamique.
            filterChain.doFilter(request, response);
            return;
        }

        // Resource = segment après /api/
        // Cas spécial: métriques dashboard par "metric"
        //
        // Endpoint visé:
        //   /api/dashboard/admin/stats/{metric}
        // On veut que la "resource" match {metric} (pour permissions dynamiques spécifiques).
        String afterApi = path.substring("/api/".length()); // ex: "analyses/..."
        String resource;

        String statsPrefix = "dashboard/admin/stats/";
        if (afterApi.startsWith(statsPrefix)) {
            String metricAndMore = afterApi.substring(statsPrefix.length()); // ex: "{metric}" ou "query/..."
            int slash = metricAndMore.indexOf('/');
            resource = (slash >= 0) ? metricAndMore.substring(0, slash) : metricAndMore;

            // Nouveau contrat générique:
            //   /api/dashboard/admin/stats/query
            // Doit être autorisé via permission dynamique "dashboard:READ" (catalogue existant).
            if ("query".equalsIgnoreCase(resource)) {
                resource = "dashboard";
            }
        } else {
            // comportement actuel: premier segment après /api/
            resource = afterApi;
            int slash = afterApi.indexOf('/');
            if (slash >= 0) {
                resource = afterApi.substring(0, slash);
            }
        }

        // Nettoyage (cas rarissime si resource vide)
        resource = (resource == null || resource.isBlank()) ? "" : resource.trim();
        if (resource.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Normalisation pour matcher plus facilement les permissions
        // ex: "audit-logs" => "audit_logs"
        String resourceNormalized = resource.replace('-', '_');

        String action = mapAction(request.getMethod()); // READ/WRITE/DELETE

        // Candidates authority formats (supports both)
        String resUpper = resourceNormalized.toUpperCase(Locale.ROOT);
        String permColon = resUpper + ":" + action;
        String permUnderscore = resUpper + "_" + action;

        // Pour l'évaluation scope/condition_type on construit l'ensemble des permissions attendues.
        // On y met colon + underscore + heuristiques "atom".
        Set<String> expectedAuthoritiesNormalized = new HashSet<>();
        expectedAuthoritiesNormalized.add(normalizeAuthority(permColon));
        expectedAuthoritiesNormalized.add(normalizeAuthority(permUnderscore));

        boolean allowed = normalizedAuthorities.contains(permColon) || normalizedAuthorities.contains(permUnderscore);

        // V1 "permission atomique" (heuristique):
        // - module = premier segment après /api/
        // - section = premier segment non-numérique après module
        // - action = READ/WRITE/DELETE (dérivé de la méthode HTTP)
        //
        // Candidats:
        // - MODULE.ACTION => ANALYSES.READ
        // - MODULE.SECTION.ACTION => PATIENTS.DOSSIER_COMPLET.READ
        if (!allowed && afterApi != null && !afterApi.isBlank()) {
            String[] segments = afterApi.split("/");
            String moduleSeg = (segments.length > 0) ? segments[0] : "";
            moduleSeg = (moduleSeg == null) ? "" : moduleSeg.trim().replace('-', '_');

            String sectionSeg = null;
            for (int i = 1; i < segments.length; i++) {
                String seg = segments[i];
                if (seg == null) continue;
                String trimmed = seg.trim();
                if (trimmed.isEmpty()) continue;
                if (trimmed.matches("^[0-9]+$")) continue; // ID numérique => skip
                sectionSeg = trimmed;
                break;
            }

            if (!moduleSeg.isBlank()) {
                String moduleUpper = moduleSeg.toUpperCase(Locale.ROOT);

                String atomPerm2 = moduleUpper + "." + action; // module.ACTION
                boolean atomAllowed = normalizedAuthorities.contains(atomPerm2);
                expectedAuthoritiesNormalized.add(normalizeAuthority(atomPerm2));

                if (!atomAllowed && sectionSeg != null && !sectionSeg.isBlank()) {
                    String sectionUpper = sectionSeg.trim().replace('-', '_').toUpperCase(Locale.ROOT);
                    String atomPerm3 = moduleUpper + "." + sectionUpper + "." + action; // module.section.ACTION
                    expectedAuthoritiesNormalized.add(normalizeAuthority(atomPerm3));
                    atomAllowed = normalizedAuthorities.contains(atomPerm3);
                }

                allowed = atomAllowed;
            }
        }

        // Option: tolérance si l'autorité est stockée avec une forme lowercase (rare)
        if (!allowed) {
            String resLower = resource.toLowerCase(Locale.ROOT).replace('-', '_').toUpperCase(Locale.ROOT);
            String tolColon = resLower + ":" + action;
            String tolUnderscore = resLower + "_" + action;
            expectedAuthoritiesNormalized.add(normalizeAuthority(tolColon));
            expectedAuthoritiesNormalized.add(normalizeAuthority(tolUnderscore));

            allowed = normalizedAuthorities.contains(tolColon) || normalizedAuthorities.contains(tolUnderscore);
        }

        if (!allowed) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"Permission requise: " + permColon + " ou " + permUnderscore + "\"}");
            return;
        }

        // =========================
        // RBAC dynamique: scope/condition_type
        // =========================
        enforceScopeConditionIfPresent(authentication, path, expectedAuthoritiesNormalized, response);
        if (response.isCommitted()) {
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void enforceScopeConditionIfPresent(
            Authentication authentication,
            String requestUri,
            Set<String> expectedAuthoritiesNormalized,
            HttpServletResponse response
    ) throws IOException {

        if (expectedAuthoritiesNormalized == null || expectedAuthoritiesNormalized.isEmpty()) {
            return;
        }

        Long patientId = extractPatientId(requestUri);

        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            return;
        }

        User user = userRepository.findByEmailWithPermissions(email).orElse(null);
        if (user == null || user.getPermissions() == null || user.getPermissions().isEmpty()) {
            return; // pas de metadata -> on laisse passer (allowed déjà vérifié)
        }

        LocalDateTime now = LocalDateTime.now();

        boolean anyConditionEnforced = false;

        for (RolePermission rp : user.getPermissions()) {
            if (rp == null) continue;

            // Filtre: permission match pour cette requête
            if (!rolePermissionMatchesExpected(rp, expectedAuthoritiesNormalized)) continue;

            if (!isPermissionActive(rp, now)) continue;

            String conditionType = rp.getConditionType();
            String scope = rp.getScope();

            boolean hasMetadata = (conditionType != null && !conditionType.isBlank()) || (scope != null && !scope.isBlank());
            if (!hasMetadata) continue;

            // Pour l'instant, on implémente la partie "patient ownership / referent / assigned"
            // (les endpoints patient contiennent déjà un ownership via PatientAccessService,
            // mais on en fait un gage supplémentaire côté RBAC dynamique).
            String ct = conditionType == null ? "" : conditionType.trim().toUpperCase(Locale.ROOT);
            String sc = scope == null ? "" : scope.trim().toUpperCase(Locale.ROOT);

            boolean mentionsPatient =
                    ct.contains("PATIENT")
                            || ct.contains("REFERENT")
                            || ct.contains("ASSIGNED")
                            || ct.contains("OWNER")
                            || ct.contains("OWNERSHIP")
                            || sc.contains("PATIENT")
                            || sc.contains("DOSSIER");

            // Si metadata existe mais ne mentionne pas de scope patient => on ne verrouille pas (phase suivante: étendre).
            if (!mentionsPatient) continue;

            anyConditionEnforced = true;

            if (patientId == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"scope/condition_type exige patientId (segment /patient/{id})\"}");
                return;
            }

            try {
                patientAccessService.assertPatientAccessStrict(patientId);
            } catch (AccessDeniedException ex) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                String safeMsg = ex.getMessage() == null ? "" : ex.getMessage().replace('"', '\'');
                response.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"scope/condition_type refusé: " + safeMsg + "\"}");
                return;
            }
        }

        // Si aucune condition pertinente n'a été appliquée, on laisse passer.
        // (allowed=true déjà vérifié sur permission dynamique)
        if (!anyConditionEnforced) {
            return;
        }
    }

    private boolean patientPortalBypass(String path, String method) {
        if (path == null || method == null) return false;
        if (!"GET".equals(method)) return false;

        // /api/patients/{id}/dossier-complet
        if (path.startsWith("/api/patients/")
                && path.endsWith("/dossier-complet")) {
            return true;
        }

        // /api/prescriptions
        if ("/api/prescriptions".equals(path)) {
            return true;
        }

        return false;
    }

    private boolean rolePermissionMatchesExpected(RolePermission rp, Set<String> expectedAuthoritiesNormalized) {
        // Format 1: rp.permission stocke directement (ex: "PATIENTS:READ")
        if (rp.getPermission() != null) {
            String permNorm = normalizeAuthority(rp.getPermission());
            if (permNorm != null && expectedAuthoritiesNormalized.contains(permNorm)) {
                return true;
            }
        }

        // Format 2: ressource + action (ex: "patients:READ")
        if (rp.getRessource() != null && !rp.getRessource().isBlank()
                && rp.getAction() != null && !rp.getAction().isBlank()) {
            String resAct = rp.getRessource().trim() + ":" + rp.getAction().trim();
            String resActNorm = normalizeAuthority(resAct);
            if (resActNorm != null && expectedAuthoritiesNormalized.contains(resActNorm)) {
                return true;
            }

            // Underscore variant (ex: "PATIENTS_READ")
            String resActUnderscore = resActNorm != null ? resActNorm.replace(':', '_') : null;
            if (resActUnderscore != null && expectedAuthoritiesNormalized.contains(resActUnderscore)) {
                return true;
            }
        }

        return false;
    }

    private boolean isPermissionActive(RolePermission rp, LocalDateTime now) {
        if (rp == null) return false;

        LocalDateTime validFrom = rp.getValidFrom();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }

        LocalDateTime validTo = rp.getValidTo();
        if (validTo != null && now.isAfter(validTo)) {
            return false;
        }

        return true;
    }

    private Long extractPatientId(String requestUri) {
        if (requestUri == null || requestUri.isBlank()) return null;

        String marker = "/patient/";
        int idx = requestUri.indexOf(marker);
        if (idx < 0) return null;

        String after = requestUri.substring(idx + marker.length()); // "{id}/..."
        int slash = after.indexOf('/');
        String first = (slash >= 0) ? after.substring(0, slash) : after;

        if (first == null) return null;
        String trimmed = first.trim();
        if (trimmed.isBlank()) return null;

        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalizeAuthority(String authority) {
        if (authority == null) return null;
        return authority.trim().replace('-', '_').toUpperCase(Locale.ROOT);
    }

    private String mapAction(String httpMethod) {
        if (httpMethod == null) return "READ";
        String m = httpMethod.toUpperCase(Locale.ROOT);

        return switch (m) {
            case "GET" -> "READ";
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "MODIFY";
            case "DELETE" -> "DELETE";
            default -> "READ";
        };
    }
}
