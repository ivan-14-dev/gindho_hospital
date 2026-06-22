package com.gindho.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditQualiteDto {
    private Long id;
    private String titre;
    private String domaine;
    private String description;
    private String auditeur;
    private LocalDateTime dateAudit;
    private String resultat;
}
