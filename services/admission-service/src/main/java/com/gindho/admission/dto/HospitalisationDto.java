package com.gindho.admission.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HospitalisationDto {
    private Long id; private Long patientId; private Long medecinId; private Long litId;
    private LocalDateTime dateAdmission; private LocalDateTime dateSortie;
    private String statut; private String motif; private String service; private String notes;
}