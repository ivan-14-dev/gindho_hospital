package com.gindho.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeleconsultationDto {
    private Long id;
    private Long patientId;
    private Long medecinId;
    private LocalDateTime datePrevu;
    private String motif;
    private String statut;
    private String lienSession;
}
