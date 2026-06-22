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
public class PatientDashboardDto {
    private Long patientId;
    private String nom;
    private String prenom;
    private Long prochainsRendezVous;
    private Long analysesEnAttente;
    private List<RendezVousDto> rendezVous;
    private List<AnalyseDto> analyses;
}