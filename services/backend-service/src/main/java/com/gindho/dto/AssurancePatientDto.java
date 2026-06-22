package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssurancePatientDto {
    private Long id;
    private String compagnie;
    private String numeroPolice;
    private String typeCouverture;
    private BigDecimal tauxPriseEnCharge;
    private BigDecimal plafondAnnuel;
    private BigDecimal montantConsomme;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean actif;
    private Long patientId;
    private String patientNom;
}
