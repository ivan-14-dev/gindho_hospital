package com.gindho.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/permissions")
public class AdminPermissionsController {

    @GetMapping("/catalog")
    public List<String> getCatalog() {
        // DynamicPermissionFilter (hybride) attend :
        // - authority sous forme "RESOURCE:READ|WRITE|DELETE"
        // - ou "RESOURCE_READ|RESOURCE_WRITE|RESOURCE_DELETE"
        //
        // Resource = premier segment après "/api/" dans l'URI
        // Action = GET => READ, POST/PUT/PATCH => WRITE, DELETE => DELETE
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
                "maladies",
                "medicaments",
                // Dashboard series métriques par graph
                "patients-by-day-30",
                "patients-by-day-20",
                "patients-by-hour-20",
                "patients-by-month-20",
                // Crosstab / matrice (frames)
                "rdv-status-medecin"
        );

        List<String> actions = List.of("READ", "WRITE", "DELETE");

        List<String> result = new ArrayList<>();
        for (String res : resources) {
            String resUpper = res.toUpperCase();
            for (String action : actions) {
                result.add(resUpper + ":" + action);
            }
        }
        return result;
    }
}
