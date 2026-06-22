package com.gindho.emr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosticDto {
    private Long id;
    private Long consultationId;
    private String codeCim10;
    private String libelle;
    private String type;
}
