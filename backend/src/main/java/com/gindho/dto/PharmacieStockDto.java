package com.gindho.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class PharmacieStockDto {
    private Long id; private String medicament; private String lot;
    private int quantite; private BigDecimal prixUnitaire;
    private LocalDate datePeremption; private LocalDate dateEntree; private boolean actif;
}