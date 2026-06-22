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
public class WaitingListDto {
    private Long id;
    private Long patientId;
    private Long medecinId;
    private LocalDateTime dateAjout;
    private int priorite;
    private String statut;
    private String motif;
}