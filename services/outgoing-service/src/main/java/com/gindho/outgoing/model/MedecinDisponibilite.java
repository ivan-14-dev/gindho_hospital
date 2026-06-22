package com.gindho.outgoing.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "medecin_disponibilites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MedecinDisponibilite extends BaseEntity {
    private Long medecinId;

    @Enumerated(EnumType.STRING)
    private DayOfWeek jourSemaine;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    private int dureeConsultationMinutes = 30;

    @Column(length = 500)
    private String notes;

    private boolean actif = true;

    public enum Periode {
        MATIN, APRES_MIDI, SOIREE
    }
}