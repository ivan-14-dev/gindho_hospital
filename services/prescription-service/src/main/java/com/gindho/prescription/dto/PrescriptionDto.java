package com.gindho.prescription.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrescriptionDto {
    private Long id;
    private Long patientId;
    private Long medecinId;
    private Long consultationId;
    private LocalDateTime datePrescription;
    private String diagnostic;
    private String instructions;
    private String statut;
}
