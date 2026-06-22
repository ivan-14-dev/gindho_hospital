package com.gindho.appointment.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilite extends BaseEntity {

    @Column(nullable = false)
    private Long medecinId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    @Column(nullable = false)
    private boolean disponible;
}