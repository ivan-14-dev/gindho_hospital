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
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Incident extends BaseEntity {
    private String titre;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String niveauGravite;
    private String declarePar;
    private LocalDateTime dateDeclaration;
    private boolean resolu;
    private LocalDateTime dateResolution;
    @Column(columnDefinition = "TEXT")
    private String actionCorrective;
}
