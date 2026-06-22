package com.gindho.emr.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "signes_vitaux")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SigneVitaux extends BaseEntity {
    private Long patientId;
    private Long hospitalisationId;
    private LocalDateTime dateReleve;
    private BigDecimal temperature;
    private Integer frequenceCardiaque;
    private Integer frequenceRespiratoire;
    private Integer saturationO2;
    private Integer tensionArterielleSys;
    private Integer tensionArterielleDia;
    @Column(columnDefinition = "TEXT")
    private String commentaire;
}
