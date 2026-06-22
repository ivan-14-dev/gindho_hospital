package com.gindho.laboratory.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AnalyseDto {
    private Long id; private Long patientId; private Long medecinId;
    private String typeAnalyse; private String code; private String description;
    private LocalDateTime datePrescription; private LocalDateTime dateResultat;
    private String resultat; private String statut; private boolean urgent;
    private String patientNom;
}