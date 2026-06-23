package com.gindho.setup.service;

import com.gindho.setup.dto.SetupRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SetupServiceImpl implements SetupService {

    public static final String STEP_INFRA_K8S = "infra_k8s";
    public static final String STEP_DATABASE = "database";
    public static final String STEP_KEYCLOAK = "keycloak";
    public static final String STEP_KAFKA = "kafka";
    public static final String STEP_MICROSERVICES = "microservices";
    public static final String STEP_FRONTEND = "frontend";
    public static final String STEP_FINALIZE = "finalize";

    @Value("${spring.datasource.url:jdbc:postgresql://postgres.infrastructure.svc.cluster.local:95432/gindho}")
    private String databaseUrl;

    @Value("${spring.datasource.username:gindho}")
    private String dbUsername;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    private final DataSource dataSource;
    private final KafkaAdmin kafkaAdmin;

    private final Map<String, Object> setupStatus = new ConcurrentHashMap<>();
    private final Map<String, String> stepStatus = new ConcurrentHashMap<>();
    private final Map<String, String> stepMessage = new ConcurrentHashMap<>();
    private final List<String> orderedSteps = List.of(
            STEP_INFRA_K8S, STEP_DATABASE, STEP_KEYCLOAK, STEP_KAFKA,
            STEP_MICROSERVICES, STEP_FRONTEND, STEP_FINALIZE
    );
    private volatile boolean running = false;

    public SetupServiceImpl(DataSource dataSource, KafkaAdmin kafkaAdmin) {
        this.dataSource = dataSource;
        this.kafkaAdmin = kafkaAdmin;
    }

    @PostConstruct
    public void init() {
        resetSetup();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------
    @Override
    public Map<String, Object> getSetupStatus() {
        synchronized (setupStatus) {
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("setupCompleted", Boolean.TRUE.equals(setupStatus.get("completed")));
            resp.put("hospitalName", setupStatus.get("hospitalName"));
            resp.put("domainName", setupStatus.get("domainName"));
            resp.put("email", setupStatus.get("email"));
            resp.put("applicationUrl", setupStatus.get("applicationUrl"));
            resp.put("startedAt", setupStatus.get("startedAt"));
            resp.put("startTimeFormatted", setupStatus.get("startTimeFormatted"));
            resp.put("endTimeFormatted", setupStatus.get("endTimeFormatted") != null ? setupStatus.get("endTimeFormatted") : "");
            resp.put("running", running);
            resp.put("selectedServices", setupStatus.get("selectedServices"));
            resp.put("steps", buildStepsView());
            return resp;
        }
    }

    @Override
    public Map<String, Object> startSetup(SetupRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (running) {
            result.put("success", false);
            result.put("message", "Setup déjà en cours");
            return result;
        }
        synchronized (setupStatus) {
            setupStatus.put("hospitalName", request.getHospitalName());
            setupStatus.put("domainName", request.getDomainName());
            setupStatus.put("email", request.getEmail());
            setupStatus.put("applicationUrl", request.getApplicationUrl());
            setupStatus.put("selectedServices", request.getServices());
            setupStatus.put("completed", false);
            setupStatus.put("startedAt", System.currentTimeMillis());
            setupStatus.put("startTimeFormatted", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            setupStatus.remove("endTimeFormatted");
        }
        resetSteps();
        new Thread(() -> {
            running = true;
            try {
                executeSetup(request);
            } catch (Exception e) {
                markStep(STEP_FINALIZE, false, "error", "Erreur globale : " + safe(e.getMessage()));
            } finally {
                running = false;
            }
        }, "setup-worker").start();
        result.put("success", true);
        result.put("message", "Installation démarrée");
        result.put("data", getSetupStatus());
        return result;
    }

    @Override
    public Map<String, Object> getProgress() {
        Map<String, Object> progress = new LinkedHashMap<>();
        progress.put("steps", buildStepsView());
        progress.put("overallProgress", computeOverallProgress());
        return progress;
    }

    @Override
    public Map<String, Object> resetSetup() {
        Map<String, Object> result = new LinkedHashMap<>();
        resetSteps();
        synchronized (setupStatus) {
            setupStatus.put("completed", false);
            setupStatus.remove("endTimeFormatted");
        }
        result.put("success", true);
        result.put("message", "Setup réinitialisé");
        return result;
    }

    // -------------------------------------------------------------------------
    // Core flow
    // -------------------------------------------------------------------------
    private void executeSetup(SetupRequest request) {
        Map<String, Exception> failures = new LinkedHashMap<>();
        try {
            markStep(STEP_INFRA_K8S, true, "running", "Création des namespaces et déploiement des manifests K8s...");
            deployK8sInfrastructure(request);

            markStep(STEP_DATABASE, true, "running", "Création des schémas PostgreSQL...");
            initDatabaseSchemas();

            markStep(STEP_KEYCLOAK, true, "running", "Configuration du realm Keycloak...");
            configureKeycloak(request);

            markStep(STEP_KAFKA, true, "running", "Création des topics Kafka via Strimzi...");
            createKafkaTopics();

            markStep(STEP_MICROSERVICES, true, "running", "Déploiement des microservices sélectionnés...");
            deploySelectedMicroservices(request);

            markStep(STEP_FRONTEND, true, "running", "Déploiement du frontend gindho-frontend...");
            deployFrontend(request);

            markStep(STEP_FINALIZE, true, "running", "Marquage du setup comme terminé...");
            synchronized (setupStatus) {
                setupStatus.put("completed", true);
                setupStatus.put("endTimeFormatted", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            }
            markStep(STEP_FINALIZE, true, "completed", "Installation terminée avec succès ✅");
        } catch (Exception e) {
            markStep(STEP_FINALIZE, false, "error", "Erreur : " + safe(e.getMessage()));
            failures.put("global", e);
        }
    }

    // -------------------------------------------------------------------------
    // Steps
    // -------------------------------------------------------------------------
    private void deployK8sInfrastructure(SetupRequest request) throws Exception {
        markStep(STEP_INFRA_K8S, true, "completed",
                "Namespaces et manifests déjà appliqués par deploy.sh ; Etape K8s ignorée (by design).");
    }

    private void initDatabaseSchemas() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = loadResource("db/init-all-schemas.sql");
            if (sql == null || sql.isBlank()) {
                sql = """
                        CREATE TABLE IF NOT EXISTS users (
                            id BIGSERIAL PRIMARY KEY,
                            cree_le TIMESTAMP NOT NULL DEFAULT NOW(),
                            mis_a_jour_le TIMESTAMP NOT NULL DEFAULT NOW(),
                            nom VARCHAR(255) NOT NULL, prenom VARCHAR(255) NOT NULL,
                            email VARCHAR(255) NOT NULL UNIQUE,
                            mot_de_passe_hash VARCHAR(255) NOT NULL,
                            role VARCHAR(50) NOT NULL DEFAULT 'UTILISATEUR_SECONDAIRE',
                            actif BOOLEAN NOT NULL DEFAULT TRUE
                        );
                        CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
                        CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
                        """;
            }
            String[] stmts = sql.split(";");
            int count = 0;
            for (String s : stmts) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        stmt.execute(trimmed);
                        count++;
                    } catch (Exception e) {
                        if (!safe(e.getMessage()).toLowerCase().contains("already exists")) throw e;
                    }
                }
            }
            markStep(STEP_DATABASE, true, "completed", count + " instructions SQL exécutées.");
        } catch (Exception e) {
            markStep(STEP_DATABASE, false, "error", e.getMessage());
            throw e;
        }
    }

    private void configureKeycloak(SetupRequest request) throws Exception {
        String keycloakUrl = request.getKeycloakUrl() != null ? request.getKeycloakUrl()
                : "http://keycloak.infrastructure.svc.cluster.local:9001";
        String adminUser = request.getKeycloakAdminUsername() != null ? request.getKeycloakAdminUsername() : "admin";
        String adminPass = request.getKeycloakAdminPassword() != null ? request.getKeycloakAdminPassword() : "admin123";

        try {
            String token = fetchKeycloakToken(keycloakUrl, adminUser, adminPass);
            createRealm(keycloakUrl, token, request);
            markStep(STEP_KEYCLOAK, true, "completed", "Realm '" + request.getDomainName() + "' + client créés.");
        } catch (Exception e) {
            markStep(STEP_KEYCLOAK, false, "error", e.getMessage());
            throw e;
        }
    }

    private String fetchKeycloakToken(String keycloakUrl, String adminUser, String adminPass) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String body = "grant_type=password&client_id=admin-cli&username=" + urlEncode(adminUser) + "&password=" + urlEncode(adminPass);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(keycloakUrl + "/realms/master/protocol/openid-connect/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(java.time.Duration.ofSeconds(30))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new RuntimeException("Keycloak auth échoué (HTTP " + resp.statusCode() + ") : " + resp.body());
        }
        String token = extractJson(resp.body(), "access_token");
        if (token == null || token.isBlank()) throw new RuntimeException("Token Keycloak vide.");
        return token;
    }

    private void createRealm(String keycloakUrl, String token, SetupRequest request) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String realmName = request.getDomainName().replace(".", "_");
        String realmJson = """
                {
                  "realm": "%s",
                  "enabled": true,
                  "displayName": "%s",
                  "accessTokenLifespan": 3600,
                  "clients": [
                    {
                      "clientId": "gindho-frontend",
                      "enabled": true,
                      "protocol": "openid-connect",
                      "publicClient": true,
                      "redirectUris": ["%s/*", "http://localhost:9300/*"],
                      "webOrigins": ["*"],
                      "standardFlowEnabled": true
                    }
                  ],
                  "users": [
                    {
                      "username": "%s",
                      "email": "%s",
                      "enabled": true,
                      "emailVerified": true,
                      "firstName": "%s",
                      "lastName": "%s",
                      "credentials": [
                        {
                          "type": "password",
                          "value": "%s",
                          "temporary": false
                        }
                      ],
                      "realmRoles": ["SUPER_ADMIN"]
                    }
                  ]
                }
                """.formatted(
                realmName,
                safe(request.getHospitalName()),
                safe(request.getApplicationUrl()),
                safe(request.getSuperAdminUsername()),
                safe(request.getSuperAdminEmail()),
                safe(request.getSuperAdminFirstName()),
                safe(request.getSuperAdminLastName()),
                safe(request.getSuperAdminPassword())
        );
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(keycloakUrl + "/admin/realms"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(realmJson))
                .timeout(java.time.Duration.ofSeconds(30))
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 201 && resp.statusCode() != 409) {
            throw new RuntimeException("Création du realm échoué (HTTP " + resp.statusCode() + ") : " + resp.body());
        }
    }

    private void createKafkaTopics() throws Exception {
        List<String> topics = List.of(
                "patient-events", "appointment-events", "medical-record-events",
                "billing-events", "audit-events", "dlq-global", "public-event-created",
                "doctor-availability-updated", "patient-transfer-requested"
        );
        int created = 0;
        for (String topic : topics) {
            try {
                createKafkaTopic(topic);
                created++;
            } catch (Exception e) {
                // Ignore "already exists"
                if (!safe(e.getMessage()).toLowerCase().contains("already exists")) {
                    throw e;
                }
            }
        }
        markStep(STEP_KAFKA, true, "completed", created + " topics Kafka créés/traités.");
    }

    private void createKafkaTopic(String topicName) throws Exception {
        String yaml = """
                apiVersion: kafka.strimzi.io/v1beta2
                kind: KafkaTopic
                metadata:
                  name: %s
                  namespace: infrastructure
                  labels:
                    strimzi.io/cluster: gindho-kafka
                spec:
                  partitions: 3
                  replicas: 1
                """.formatted(topicName);
        Process process = Runtime.getRuntime().exec(new String[]{"kubectl", "apply", "-f", "-"});
        try (var os = process.getOutputStream()) {
            os.write(yaml.getBytes());
            os.flush();
        }
        int code = process.waitFor();
        if (code != 0) {
            throw new RuntimeException("kubectl apply KafkaTopic a échoué pour " + topicName);
        }
    }

    private void deploySelectedMicroservices(SetupRequest request) throws Exception {
        List<String> services = request.getServices();
        if (services == null || services.isEmpty()) {
            markStep(STEP_MICROSERVICES, true, "completed", "Aucun service sélectionné, étape ignorée.");
            return;
        }
        int ok = 0;
        for (String svc : services) {
            try {
                deployService(svc);
                ok++;
            } catch (Exception e) {
                if (!safe(e.getMessage()).toLowerCase().contains("already exists")) {
                    throw new RuntimeException("Erreur déploiement " + svc + " : " + e.getMessage(), e);
                }
                ok++;
            }
        }
        markStep(STEP_MICROSERVICES, true, "completed", ok + " services déployés.");
    }

    private void deployService(String serviceName) throws Exception {
        String resourceDir = "k8s/" + serviceName + "-namespace/";
        var walker = new org.springframework.core.io.support.PathMatchingResourcePatternResolver();
        var resources = walker.getResources("classpath:/" + resourceDir + "*.yaml");
        if (resources.length == 0) {
            throw new RuntimeException("Aucun manifest K8s trouvé pour le service " + serviceName + " dans " + resourceDir);
        }
        for (var res : resources) {
            String yaml = new String(res.getInputStream().readAllBytes());
            Process process = Runtime.getRuntime().exec(new String[]{"kubectl", "apply", "-f", "-"});
            try (var os = process.getOutputStream()) {
                os.write(yaml.getBytes());
                os.flush();
            }
            int code = process.waitFor();
            if (code != 0) {
                String err = new String(process.getErrorStream().readAllBytes());
                if (!err.toLowerCase().contains("already exists") && !err.toLowerCase().contains("unchanged")) {
                    throw new RuntimeException("kubectl apply a échoué pour " + res.getFilename() + " : " + err);
                }
            }
        }
    }

    private void deployFrontend(SetupRequest request) throws Exception {
        String frontendUrl = request.getFrontendUrl();
        if (frontendUrl == null || frontendUrl.isBlank()) {
            markStep(STEP_FRONTEND, true, "completed", "Aucune URL frontend fournie, étape ignorée.");
            return;
        }
        if (!isReachable(frontendUrl)) {
            throw new RuntimeException("Le frontend (" + frontendUrl + ") n'est pas joignable.");
        }
        markStep(STEP_FRONTEND, true, "completed", "Frontend accessible à " + frontendUrl + ".");
    }

    // -------------------------------------------------------------------------
    // Utils
    // -------------------------------------------------------------------------
    private boolean isReachable(String url) throws Exception {
        try {
            HttpURLConnection c = (HttpURLConnection) new URI(url).toURL().openConnection();
            c.setConnectTimeout(3000);
            c.setReadTimeout(3000);
            c.connect();
            int code = c.getResponseCode();
            return code >= 200 && code < 500;
        } catch (Exception e) {
            return false;
        }
    }

    private void markStep(String key, boolean completed, String status, String message) {
        stepStatus.put(key, status);
        stepMessage.put(key, message);
    }

    private void resetSteps() {
        stepStatus.clear();
        stepMessage.clear();
        for (String step : orderedSteps) {
            stepStatus.put(step, "pending");
            stepMessage.put(step, "");
        }
    }

    private String loadResource(String path) throws IOException {
        try (var is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) return null;
            return new String(is.readAllBytes());
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String urlEncode(String s) {
        return s == null ? "" : s.replace(" ", "%20");
    }

    private static String extractJson(String json, String field) {
        String pattern = "\"" + field + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private List<Map<String, Object>> buildStepsView() {
        List<Map<String, Object>> view = new ArrayList<>();
        for (String key : orderedSteps) {
            Map<String, Object> step = new LinkedHashMap<>();
            step.put("key", key);
            step.put("status", stepStatus.getOrDefault(key, "pending"));
            step.put("message", stepMessage.getOrDefault(key, ""));
            step.put("completed", "completed".equals(stepStatus.get(key)));
            view.add(step);
        }
        return view;
    }

    private double computeOverallProgress() {
        long completed = orderedSteps.stream()
                .filter(s -> "completed".equals(stepStatus.get(s)))
                .count();
        return orderedSteps.isEmpty() ? 0 : (completed * 100.0) / orderedSteps.size();
    }
}
