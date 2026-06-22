package com.gindho.appointment.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "teleconsultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teleconsultation extends BaseEntity {
    private Long patientId;
    private Long medecinId;
    private LocalDateTime datePrevu;
    private String motif;
    private String statut = "DEMANDEE";
    @Column(columnDefinition = "TEXT")
    private String lienSession;
}
