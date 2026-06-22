package com.gindho.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisponibiliteDto {
    private Long id;
    private Long medecinId;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private boolean disponible;
}