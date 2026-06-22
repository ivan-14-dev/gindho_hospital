package com.gindho.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousDto {
    private Long id;
    private Long patientId;
    private Long medecinId;
    private LocalDateTime dateHeureDebut;
    private LocalDateTime dateHeureFin;
    private String motif;
    private String statut;
    private String notes;
    private String typeConsultation;
}