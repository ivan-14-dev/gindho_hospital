package com.gindho.emergency.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmergencyTriageDto {
    private Long id; private Long patientId; private Long medecinId;
    private String niveauTriage; private String description; private String priseEnCharge;
    private LocalDateTime dateArrivee; private String statut;
}