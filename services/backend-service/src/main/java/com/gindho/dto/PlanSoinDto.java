package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanSoinDto {
    private Long id;
    private String typeSoin;
    private String description;
    private LocalDateTime dateSoin;
    private LocalDateTime dateRealisation;
    private boolean realise;
    private String notesInfirmier;
    private Long hospitalisationId;
    private Long patientId;
    private String patientNom;
}
