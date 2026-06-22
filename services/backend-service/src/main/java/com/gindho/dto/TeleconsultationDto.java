package com.gindho.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class TeleconsultationDto {
    private Long id; private LocalDateTime dateDebut; private LocalDateTime dateFin;
    private String statut; private String lienVideo; private String notes;
    private Long patientId; private String patientNom;
    private Long medecinId; private String medecinNom;
}