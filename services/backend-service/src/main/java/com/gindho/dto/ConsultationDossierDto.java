package com.gindho.dto;

import com.gindho.model.RendezVous;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationDossierDto {
    private Long rendezVousId;

    private LocalDateTime dateHeureDebut;
    private LocalDateTime dateHeureFin;

    private RendezVous.StatutRDV statut;
    private String motif;
    private String notes;

    // Contenu du dossier médical lié à ce RDV (diagnostic/traitement/observations)
    private Long dossierMedicalId;
    private LocalDate dateConsultation;
    private String diagnostic;
    private String traitement;
    private String observations;

    private List<AnalyseDto> analyses;
    private List<PrescriptionDto> prescriptions;
}
