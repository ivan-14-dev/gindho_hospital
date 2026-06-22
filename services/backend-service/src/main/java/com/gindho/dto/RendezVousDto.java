package com.gindho.dto;

import com.gindho.model.RendezVous;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RendezVousDto {
    private Long id;
    private LocalDateTime dateHeureDebut;
    private LocalDateTime dateHeureFin;
    private RendezVous.StatutRDV statut;
    private String motif;
    private String notes;
    private Long patientId;
    private Long medecinId;
    private String patientNom;
    private String medecinNom;
}
