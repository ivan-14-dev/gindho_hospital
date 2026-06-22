package com.gindho.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDashboardDto {
    private Long patientId;
    private String nomComplet;
    private int consultations;
    private int rendezVous;
    private int hospitalisations;
    private int documents;
}
