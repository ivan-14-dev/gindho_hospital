package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Paiement extends BaseEntity {

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut;

    /**
     * Anti double-commit / idempotency key.
     * Si même transactionRef => on considère que c’est le même paiement.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String transactionRef;

    private LocalDateTime datePaiement;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facture_id")
    private Facture facture;

    public enum ModePaiement {
        MOBILE_MONEY,
        CARTE_BANCAIRE,
        ESPECES,
        AUTRE
    }

    public enum StatutPaiement {
        EN_ATTENTE,
        CONFIRME,
        ANNULE
    }
}
