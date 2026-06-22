package com.gindho.ambulance.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ambulances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ambulance extends BaseEntity {

    public enum TypeAmbulance {
        SMUR, AMBULANCE, VEHICULE_MEDICALISE
    }

    public enum StatutAmbulance {
        DISPONIBLE, EN_MISSION, MAINTENANCE, HORS_SERVICE
    }

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String immatriculation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAmbulance type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAmbulance statut;

    @Column(columnDefinition = "TEXT")
    private String equipements;

    private Long conducteurId;

    @Column(columnDefinition = "TEXT")
    private String personnel;

    private LocalDate dateDerniereMaintenance;

    private int kilometrage;

    private Double latitude;

    private Double longitude;

    private LocalDateTime derniereMiseAJourPosition;
}