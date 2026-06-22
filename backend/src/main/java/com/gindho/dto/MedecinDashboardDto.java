package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedecinDashboardDto {
    private Long medecinId;
    private String nom;
    private String prenom;
    private String specialisation;
    private Long rendezVousAujourdhui;
    private Long rendezVousEnAttente;
    private Long totalPatients;
    private List<RendezVousDto> rendezVous;
}