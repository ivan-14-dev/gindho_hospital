package com.gindho.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class StockConsommableDto {
    private Long id; private String nom; private String categorie;
    private int quantite; private int seuilAlerte;
    private BigDecimal prixUnitaire; private LocalDate datePeremption; private boolean actif;
}