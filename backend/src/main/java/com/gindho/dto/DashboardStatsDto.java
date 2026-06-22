package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDto {
    private Long totalPatients;
    private Long totalMedecins;
    private Long totalRendezVous;
    private Long rendezVousEnAttente;
    private Long rendezVousAujourdhui;
    private Double totalRevenus;
}