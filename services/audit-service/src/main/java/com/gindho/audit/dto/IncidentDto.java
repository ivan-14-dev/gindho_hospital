package com.gindho.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidentDto {
    private Long id;
    private String titre;
    private String description;
    private String niveauGravite;
    private String declarePar;
    private LocalDateTime dateDeclaration;
    private boolean resolu;
    private LocalDateTime dateResolution;
    private String actionCorrective;
}
