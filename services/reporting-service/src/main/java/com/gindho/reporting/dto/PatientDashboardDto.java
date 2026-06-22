package com.gindho.reporting.dto;

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
    private int rendezVous;
    private int consultations;
    private int hospitalisations;
}
