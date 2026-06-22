package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Plan de soins infirmiers pour un patient hospitalisé.
 * Définit les soins à prodiguer (programmés et réalisés).
 */
@Entity
@Table(name = "plans_soins", indexes = {
    @Index(name = "idx_plan_soin_hosp", columnList = "hospitalisation_id"),
    @Index(name = "idx_plan_soin_date", columnList = "date_soin")
})
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlanSoin extends BaseEntity {

    @Column(name = "type_soin", nullable = false, length = 255)
    private String typeSoin;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "date_soin", nullable = false)
    private LocalDateTime dateSoin;

    @Column(name = "date_realisation")
    private LocalDateTime dateRealisation;

    @Column(name = "realise", nullable = false)
    private boolean realise = false;

    @Column(name = "notes_infirmier", length = 2000)
    private String notesInfirmier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospitalisation_id", nullable = false)
    private Hospitalisation hospitalisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
}
