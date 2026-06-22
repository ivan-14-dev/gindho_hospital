package com.gindho.insurance.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
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
