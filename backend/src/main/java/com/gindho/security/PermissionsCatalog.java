package com.gindho.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PermissionsCatalog {

    private PermissionsCatalog() {
    }

    /**
     * Catalogue utilisé par l'UI pour afficher les permissions dynamiques.
     * Doit correspondre exactement au format attendu par DynamicPermissionFilter:
     *   RESOURCE:READ|WRITE|DELETE
     */
    public static List<String> getCatalog() {
        List<String> resources = List.of(
                "patients",
                "medecins",
                "rendezvous",
                "analyses",
                "prescriptions",
                "revenus",
                "factures",
                "paiements",
                "chambres",
                "lits",
                "hospitalisations",
                "dashboard",
                "disponibilites",
                "audit-logs",
                "admin-users",
                "maladies",
                "medicaments",
                
                "assurances",
                "rh",
                "evenements",
                "rondes",
                "bloc",
                "qualite",
                "incidents",
                "equipements",
                "ambulances",

                // Dashboard series métriques par graph
                "patients-by-day-30",
                "patients-by-day-20",
                "patients-by-hour-20",
                "patients-by-month-20",
                // Crosstab / matrice (frames)
                "rdv-status-medecin"
        );

        List<String> actions = List.of("CREATE", "READ", "MODIFY", "WRITE", "DELETE");

        List<String> result = new ArrayList<>();
        for (String res : resources) {
            String resUpper = res.toUpperCase(Locale.ROOT);
            for (String action : actions) {
                result.add(resUpper + ":" + action);
            }
        }
        return result;
    }
}
