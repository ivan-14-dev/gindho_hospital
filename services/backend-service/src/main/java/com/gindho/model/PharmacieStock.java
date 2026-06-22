package com.gindho.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pharmacie_stocks")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PharmacieStock extends BaseEntity {
    @Column(nullable = false, length = 255)
    private String medicament;

    @Column(nullable = false, length = 100)
    private String lot;

    @Column(nullable = false)
    private int quantite;

    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "date_peremption")
    private LocalDate datePeremption;

    @Column(name = "date_entree")
    private LocalDate dateEntree;

    @Column(nullable = false)
    private boolean actif = true;
}
