package com.gindho.quality.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audits_qualite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditQualite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    private String service;
    private String auditeur;
    private LocalDateTime dateAudit;
    private String statut;
    private String recommandations;
}