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
public class AnalyseDto {
    private Long id;
    private String typeAnalyse;
    private String resultat;
    private String observation;
    private LocalDateTime dateAnalyse;
    private Boolean urgent;
    private Long patientId;
    private Long medecinId;
    private String patientNom;
    private String medecinNom;
}
