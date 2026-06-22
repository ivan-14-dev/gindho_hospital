package com.gindho.emr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SigneVitauxDto {
    private Long id;
    private Long patientId;
    private Long hospitalisationId;
    private LocalDateTime dateReleve;
    private BigDecimal temperature;
    private Integer frequenceCardiaque;
    private Integer frequenceRespiratoire;
    private Integer saturationO2;
    private Integer tensionArterielleSys;
    private Integer tensionArterielleDia;
    private String commentaire;
}
