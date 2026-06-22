package com.gindho.patient.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "assurances_patient")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AssurancePatient extends BaseEntity {
    private Long patientId;
    private String compagnie;
    @Column(name = "numero_police") private String numeroPolice;
    private LocalDate validiteDebut;
    private LocalDate validiteFin;
    private Double couverture;
    private boolean actif = true;
}