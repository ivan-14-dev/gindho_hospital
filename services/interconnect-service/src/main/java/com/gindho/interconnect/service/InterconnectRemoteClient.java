package com.gindho.interconnect.service;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gindho.interconnect.model.HospitalPartner;
import com.gindho.interconnect.model.InterHospitalTransfer;

import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class InterconnectRemoteClient {

    private final WebClient.Builder webClientBuilder;

    public InterconnectRemoteClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Notifie un hôpital partenaire d'un transfert entrant.
     * Appelle l'endpoint /api/interconnect/public/incoming-transfer sur l'hôpital cible.
     */
    public boolean notifyTransfer(HospitalPartner target, InterHospitalTransfer transfer) {
        String url = target.getBaseUrl() + "/api/interconnect/public/incoming-transfer";

        Map<String, Object> payload = Map.of(
                "transferRef", transfer.getTransferRef(),
                "patientId", transfer.getPatientId(),
                "patientName", transfer.getPatientName(),
                "sourceHospitalId", transfer.getSourceHospitalId(),
                "sourceHospitalName", transfer.getSourceHospitalName(),
                "transferType", transfer.getTransferType().name(),
                "reason", transfer.getReason() != null ? transfer.getReason() : "",
                "medicalSummary", transfer.getMedicalSummary() != null ? transfer.getMedicalSummary() : ""
        );

        try {
            String response = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-Hospital-API-Key", target.getApiKey())
                    .header("X-Hospital-ID", target.getHospitalId())
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)))
                    .block(Duration.ofSeconds(10));

            log.debug("Remote notification sent to {}: response={}", target.getName(), response);
            return true;
        } catch (Exception e) {
            log.error("Failed to notify hospital {} at {}: {}", target.getName(), target.getBaseUrl(), e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si un hôpital partenaire est joignable (heartbeat).
     */
    public boolean checkHealth(HospitalPartner partner) {
        String url = partner.getBaseUrl() + "/actuator/health";

        try {
            String response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .header("X-Hospital-API-Key", partner.getApiKey())
                    .header("X-Hospital-ID", partner.getHospitalId())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(5));

            return response != null && response.contains("UP");
        } catch (Exception e) {
            log.warn("Health check failed for {}: {}", partner.getName(), e.getMessage());
            return false;
        }
    }

    /**
     * Envoie des données patient chiffrées à un hôpital partenaire.
     */
    public boolean sendPatientData(HospitalPartner target, String patientId, String encryptedData) {
        String url = target.getBaseUrl() + "/api/interconnect/public/receive-patient-data";

        Map<String, Object> payload = Map.of(
                "patientId", patientId,
                "encryptedData", encryptedData,
                "sourceHospitalId", target.getHospitalId()
        );

        try {
            webClientBuilder.build()
                    .post()
                    .uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-Hospital-API-Key", target.getApiKey())
                    .header("X-Hospital-ID", target.getHospitalId())
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            return true;
        } catch (Exception e) {
            log.error("Failed to send patient data to {}: {}", target.getName(), e.getMessage());
            return false;
        }
    }
}