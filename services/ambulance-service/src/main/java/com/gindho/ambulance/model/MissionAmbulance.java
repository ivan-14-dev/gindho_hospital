package com.gindho.ambulance.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "missions_ambulance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MissionAmbulance extends BaseEntity {

    public enum TypeMission {
        URGENCE, TRANSFERT, TRANSPORT_PROGRAMME
    }

    public enum StatutMission {
        PLANIFIEE, EN_COURS, TERMINEE, ANNULEE
    }

    @Column(nullable = false)
    private Long ambulanceId;

    private Long patientId;

    @Column(nullable = false)
    private String origine;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime dateDepart;

    private LocalDateTime dateArrivee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMission typeMission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMission statut;

    @Column(columnDefinition = "TEXT")
    private String notes;
}