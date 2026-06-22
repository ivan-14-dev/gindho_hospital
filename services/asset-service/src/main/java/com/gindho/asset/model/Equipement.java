package com.gindho.asset.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "equipements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipement extends BaseEntity {

    public enum StatutEquipement {
        ACTIF, EN_MAINTENANCE, HORS_SERVICE, REFORME
    }

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String nom;

    private String type;

    private String marque;

    private String modele;

    private String numeroSerie;

    private LocalDate dateAcquisition;

    @Column(precision = 15, scale = 2)
    private BigDecimal valeurAchat;

    private int dureeVie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEquipement statut;

    private String localisation;

    private Long responsableId;

    @Column(columnDefinition = "TEXT")
    private String notes;
}