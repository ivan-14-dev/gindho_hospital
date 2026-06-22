package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDto {
    private Long id;
    private String medicament;
    private String posologie;
    private String duree;
    private LocalDate dateEmission;
    private Long dossierMedicalId;
    private Long patientId;
    private String patientNom;
    private String medecinNom;
}
