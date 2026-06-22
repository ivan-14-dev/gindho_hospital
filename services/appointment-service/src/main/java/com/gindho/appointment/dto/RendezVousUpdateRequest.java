package com.gindho.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousUpdateRequest {
    private Long patientId;
    private Long medecinId;
    private java.time.LocalDateTime dateHeureDebut;
    private java.time.LocalDateTime dateHeureFin;
    private String motif;
    private String statut;
    private String notes;
    private String typeConsultation;
}
