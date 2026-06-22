package com.gindho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lits", indexes = {
        @Index(name = "idx_lits_actif", columnList = "actif"),
        @Index(name = "idx_lits_numero_chambre", columnList = "chambre_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Lit extends BaseEntity {

    @Column(name = "numero_lit", nullable = false)
    private String numeroLit;

    @Column(nullable = false)
    private boolean actif = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutLit statut = StatutLit.DISPONIBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chambre_id", nullable = false)
    private Chambre chambre;

    public enum StatutLit {
        DISPONIBLE,
        OCCUPE,
        INDISPONIBLE
    }
}
