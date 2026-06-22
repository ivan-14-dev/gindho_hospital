package com.gindho.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedecinDashboardDto {
    private Long medecinId;
    private int rendezVous;
    private int consultations;
    private int patients;
}
