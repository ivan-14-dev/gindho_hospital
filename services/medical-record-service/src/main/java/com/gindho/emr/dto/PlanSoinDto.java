package com.gindho.emr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanSoinDto {
    private Long id;
    private Long patientId;
    private Long hospitalisationId;
    private String intitule;
    private String description;
    private LocalDateTime datePrevu;
    private boolean realise;
    private LocalDateTime dateRealisation;
    private String notesRealisation;
}
