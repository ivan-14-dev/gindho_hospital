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
public class SigneVitauxDto {
    private Long id;
    private Double temperature;
    private Integer tensionSystolique;
    private Integer tensionDiastolique;
    private Integer frequenceCardiaque;
    private Integer frequenceRespiratoire;
    private Integer saturationOxygen;
    private Double glycemie;
    private Double poids;
    private LocalDateTime dateReleve;
    private String notes;
    private Long patientId;
    private Long hospitalisationId;
    private String patientNom;
}
