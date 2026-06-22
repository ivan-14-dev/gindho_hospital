package com.gindho.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDto {
    private long totalPatients;
    private long totalMedecins;
    private long totalRendezVous;
    private long rendezVousEnAttente;
    private long rendezVousAujourdhui;
    private double totalRevenus;
    private Map<String, Object> details;
}
