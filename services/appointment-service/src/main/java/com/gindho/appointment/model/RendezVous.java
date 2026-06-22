package com.gindho.appointment.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RendezVous extends BaseEntity {

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long medecinId;

    @Column(nullable = false)
    private LocalDateTime dateHeureDebut;

    @Column(nullable = false)
    private LocalDateTime dateHeureFin;

    @Column(length = 500)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutRDV statut;

    @Column(length = 2000)
    private String notes;

    @Column(length = 20)
    private String typeConsultation;
}