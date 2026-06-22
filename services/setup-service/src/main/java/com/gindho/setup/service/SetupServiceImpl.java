package com.gindho.setup.service;

import com.gindho.setup.dto.SetupRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SetupServiceImpl implements SetupService {

    private final Map<String, Object> setupStatus = new ConcurrentHashMap<>();
    private final List<Map<String, Object>> progressSteps = new ArrayList<>();

    public SetupServiceImpl() {
        initializeSteps();
    }

    private void initializeSteps() {
        progressSteps.add(createStep("database", "Initialisation de la base de données", false));
        progressSteps.add(createStep("keycloak", "Configuration de Keycloak", false));
        progressSteps.add(createStep("superadmin", "Création du compte SuperAdmin", false));
        progressSteps.add(createStep("smtp", "Configuration SMTP", false));
        progressSteps.add(createStep("domains", "Configuration des domaines", false));
        progressSteps.add(createStep("complete", "Finalisation", false));
    }

    private Map<String, Object> createStep(String key, String description, boolean completed) {
        Map<String, Object> step = new HashMap<>();
        step.put("key", key);
        step.put("description", description);
        step.put("completed", completed);
        step.put("status", "pending");
        step.put("message", "");
        return step;
    }

    @Override
    public Map<String, Object> getSetupStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("setupCompleted", setupStatus.get("completed") != null && (Boolean) setupStatus.get("completed"));
        status.put("hospitalName", setupStatus.get("hospitalName"));
        status.put("domainName", setupStatus.get("domainName"));
        status.put("steps", progressSteps);
        return status;
    }

    @Override
    public Map<String, Object> startSetup(SetupRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            setupStatus.put("hospitalName", request.getHospitalName());
            setupStatus.put("domainName", request.getDomainName());
            setupStatus.put("email", request.getEmail());
            setupStatus.put("startedAt", System.currentTimeMillis());

            result.put("success", true);
            result.put("message", "Configuration démarrée avec succès");
            result.put("data", setupStatus);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Erreur: " + e.getMessage());
        }

        return result;
    }

    @Override
    public Map<String, Object> getProgress() {
        Map<String, Object> progress = new HashMap<>();
        progress.put("steps", progressSteps);
        progress.put("overallProgress", calculateOverallProgress());
        return progress;
    }

    private double calculateOverallProgress() {
        long completed = progressSteps.stream()
                .filter(step -> Boolean.TRUE.equals(step.get("completed")))
                .count();
        return (completed * 100.0) / progressSteps.size();
    }

    @Override
    public Map<String, Object> resetSetup() {
        Map<String, Object> result = new HashMap<>();
        setupStatus.clear();
        progressSteps.clear();
        initializeSteps();
        result.put("success", true);
        result.put("message", "Setup réinitialisé");
        return result;
    }
}