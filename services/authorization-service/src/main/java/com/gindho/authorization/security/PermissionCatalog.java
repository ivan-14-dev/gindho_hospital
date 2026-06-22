package com.gindho.authorization.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PermissionCatalog {
    private PermissionCatalog() {
    }

    public static List<String> getCatalog() {
        List<String> resources = List.of(
                "patients", "medecins", "rendezvous", "analyses", "prescriptions",
                "revenus", "factures", "paiements", "chambres", "lits", "hospitalisations",
                "dashboard", "disponibilites", "audit-logs", "admin-users", "maladies",
                "medicaments", "assurances", "rh", "evenements", "rondes", "bloc",
                "qualite", "incidents", "equipements", "ambulances", "teleconsultations",
                "patients-by-day-30", "patients-by-day-20", "patients-by-hour-20",
                "patients-by-month-20", "rdv-status-medecin"
        );
        List<String> actions = List.of("CREATE", "READ", "MODIFY", "WRITE", "DELETE");
        List<String> result = new ArrayList<>();
        for (String resource : resources) {
            String normalized = resource.toUpperCase(Locale.ROOT);
            for (String action : actions) {
                result.add(normalized + ":" + action);
            }
        }
        return result;
    }

    public static List<com.gindho.authorization.dto.PermissionCatalogEntry> getCatalogGrouped() {
        List<String> resources = List.of(
                "patients", "medecins", "rendezvous", "analyses", "prescriptions",
                "revenus", "factures", "paiements", "chambres", "lits", "hospitalisations",
                "dashboard", "disponibilites", "audit-logs", "admin-users", "maladies",
                "medicaments", "assurances", "rh", "evenements", "rondes", "bloc",
                "qualite", "incidents", "equipements", "ambulances", "teleconsultations",
                "patients-by-day-30", "patients-by-day-20", "patients-by-hour-20",
                "patients-by-month-20", "rdv-status-medecin"
        );
        List<String> actions = List.of("CREATE", "READ", "MODIFY", "WRITE", "DELETE");
        List<com.gindho.authorization.dto.PermissionCatalogEntry> result = new ArrayList<>();
        for (String resource : resources) {
            String normalized = resource.toUpperCase(Locale.ROOT);
            for (String action : actions) {
                String code = normalized + ":" + action;
                result.add(new com.gindho.authorization.dto.PermissionCatalogEntry(normalized, code));
            }
        }
        return result;
    }
}
