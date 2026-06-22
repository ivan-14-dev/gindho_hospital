package com.gindho.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "stocks_consommables")
@Data @Builder @EqualsAndHashCode(callSuper = true) @NoArgsConstructor @AllArgsConstructor
public class StockConsommable extends BaseEntity {
    @Column(nullable = false, length = 255)
    private String nom;
    @Column(name = "categorie", length = 100)
    private String categorie;
    @Column(nullable = false)
    private int quantite;
    @Column(name = "seuil_alerte", nullable = false)
    private int seuilAlerte = 10;
    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;
    @Column(name = "date_peremption")
    private LocalDate datePeremption;
    @Column(nullable = false)
    private boolean actif = true;
}