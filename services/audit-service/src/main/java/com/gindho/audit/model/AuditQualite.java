package com.gindho.audit.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audits_qualite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditQualite extends BaseEntity {
    private String titre;
    private String domaine;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String auditeur;
    private LocalDateTime dateAudit;
    private String resultat;
}
