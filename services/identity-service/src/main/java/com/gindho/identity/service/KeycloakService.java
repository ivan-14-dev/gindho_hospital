package com.gindho.identity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindho.identity.model.AppUser;
import com.gindho.identity.model.LoginResponse;
import com.gindho.identity.model.UserRepresentation;
import com.gindho.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {
    @Value("${keycloak.url:${keycloak.auth-server-url:http://localhost:9001}}")
    private String keycloakUrl;
    @Value("${keycloak.realm:gindho}")
    private String realm;
    @Value("${keycloak.client-id:${keycloak.resource:gindho-backend}}")
    private String clientId;
    @Value("${keycloak.client-secret:test-secret}")
    private String clientSecret;
    @Value("${keycloak.admin-client-id:${keycloak.client-id}}")
    private String adminClientId;
    @Value("${keycloak.admin-client-secret:${keycloak.client-secret}}")
    private String adminClientSecret;

    private final UserRepository userRepository;
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public LoginResponse login(String username, String password) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String body = "client_id=" + enc(clientId) + "&client_secret=" + enc(clientSecret)
                    + "&grant_type=password&username=" + enc(username) + "&password=" + enc(password);
            HttpEntity<String> req = new HttpEntity<>(body, headers);
            ResponseEntity<String> resp = rest.postForEntity(
                    base() + "/realms/" + realm + "/protocol/openid-connect/token", req, String.class);
            JsonNode json = mapper.readTree(resp.getBody());
            return LoginResponse.builder()
                    .token(json.path("access_token").asText())
                    .refreshToken(json.path("refresh_token").asText())
                    .expiresIn(json.path("expires_in").asLong())
                    .username(username)
                    .build();
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", username, e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public LoginResponse refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "client_id=" + enc(clientId) + "&client_secret=" + enc(clientSecret)
                + "&grant_type=refresh_token&refresh_token=" + enc(refreshToken);
        HttpEntity<String> req = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> resp = rest.postForEntity(
                    base() + "/realms/" + realm + "/protocol/openid-connect/token", req, String.class);
            JsonNode json = mapper.readTree(resp.getBody());
            return LoginResponse.builder()
                    .token(json.path("access_token").asText())
                    .refreshToken(json.path("refresh_token").asText())
                    .expiresIn(json.path("expires_in").asLong())
                    .build();
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed");
        }
    }

    public void syncUserToKeycloak(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + userId));
        String token = adminAccessToken();
        List<UserRepresentation> existing = findKeycloakUsers(user.getEmail(), token);
        Map<String, Object> payload = userPayload(user);

        if (existing.isEmpty()) {
            ResponseEntity<String> response = rest.postForEntity(usersUrl(), new HttpEntity<>(payload, jsonHeaders(token)), String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Impossible de créer l'utilisateur Keycloak pour " + user.getEmail());
            }
            return;
        }

        for (UserRepresentation keycloakUser : existing) {
            ResponseEntity<String> response = rest.exchange(
                    usersUrl() + "/" + keycloakUser.getId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(payload, jsonHeaders(token)),
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Impossible de mettre à jour l'utilisateur Keycloak " + keycloakUser.getId());
            }
        }
    }

    public void deleteUserFromKeycloak(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + userId));
        String token = adminAccessToken();
        List<UserRepresentation> existing = findKeycloakUsers(user.getEmail(), token);
        for (UserRepresentation keycloakUser : existing) {
            ResponseEntity<String> response = rest.exchange(
                    usersUrl() + "/" + keycloakUser.getId(),
                    HttpMethod.DELETE,
                    new HttpEntity<>(jsonHeaders(token)),
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Impossible de supprimer l'utilisateur Keycloak " + keycloakUser.getId());
            }
        }
    }

    private Map<String, Object> userPayload(AppUser user) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("username", user.getEmail());
        payload.put("email", user.getEmail());
        payload.put("firstName", valueOr(user.getPrenom(), user.getEmail()));
        payload.put("lastName", valueOr(user.getNom(), user.getEmail()));
        payload.put("enabled", user.isActif());

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("role", List.of(user.getRole().name()));
        payload.put("attributes", attributes);
        return payload;
    }

    private List<UserRepresentation> findKeycloakUsers(String email, String token) {
        try {
            ResponseEntity<String> response = rest.exchange(
                    usersUrl() + "?email=" + enc(email) + "&exact=true",
                    HttpMethod.GET,
                    new HttpEntity<>(jsonHeaders(token)),
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return List.of();
            }
            JsonNode root = mapper.readTree(response.getBody());
            if (!root.isArray()) {
                return List.of();
            }
            List<UserRepresentation> users = new ArrayList<>();
            for (JsonNode node : root) {
                users.add(UserRepresentation.builder()
                        .id(node.path("id").asText())
                        .username(node.path("username").asText(node.path("email").asText()))
                        .email(node.path("email").asText())
                        .firstName(node.path("firstName").asText())
                        .lastName(node.path("lastName").asText())
                        .enabled(node.path("enabled").asBoolean(true))
                        .build());
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException("Recherche Keycloak échouée pour " + email, e);
        }
    }

    private String adminAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String body = "client_id=" + enc(adminClientId)
                    + "&client_secret=" + enc(adminClientSecret)
                    + "&grant_type=client_credentials";
            ResponseEntity<String> response = rest.postForEntity(
                    base() + "/realms/" + realm + "/protocol/openid-connect/token",
                    new HttpEntity<>(body, headers),
                    String.class
            );
            if (response.getBody() == null) {
                throw new IllegalStateException("Réponse Keycloak vide");
            }
            JsonNode json = mapper.readTree(response.getBody());
            String token = json.path("access_token").asText();
            if (token.isBlank()) {
                throw new IllegalStateException("Token admin Keycloak manquant");
            }
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Authentification admin Keycloak échouée: " + e.getMessage(), e);
        }
    }

    private HttpHeaders jsonHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private String usersUrl() {
        return base() + "/admin/realms/" + realm + "/users";
    }

    private String base() {
        return keycloakUrl.endsWith("/") ? keycloakUrl.substring(0, keycloakUrl.length() - 1) : keycloakUrl;
    }

    private String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String enc(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
