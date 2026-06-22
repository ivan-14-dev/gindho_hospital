package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Relevé de signes vitaux d'un patient hospitalisé ou en consultation.
 */
@Entity
@Table(name = "signes_vitaux", indexes = {
    @Index(name = "idx_signe_patient_id", columnList = "patient_id"),
    @Index(name = "idx_signe_date_releve", columnList = "date_releve")
})
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SigneVitaux extends BaseEntity {

    /** Température corporelle en °C */
    @Column(name = "temperature")
    private Double temperature;

    /** Tension artérielle systolique (mmHg) */
    @Column(name = "tension_systolique")
    private Integer tensionSystolique;

    /** Tension artérielle diastolique (mmHg) */
    @Column(name = "tension_diastolique")
    private Integer tensionDiastolique;

    /** Fréquence cardiaque (bpm) */
    @Column(name = "frequence_cardiaque")
    private Integer frequenceCardiaque;

    /** Fréquence respiratoire (respirations/min) */
    @Column(name = "frequence_respiratoire")
    private Integer frequenceRespiratoire;

    /** Saturation en oxygène (%) */
    @Column(name = "saturation_oxygen")
    private Integer saturationOxygen;

    /** Glycémie (g/L) */
    @Column(name = "glycemie")
    private Double glycemie;

    /** Poids (kg) */
    @Column(name = "poids")
    private Double poids;

    /** Date et heure du relevé */
    @Column(name = "date_releve", nullable = false)
    private LocalDateTime dateReleve;

    /** Notes optionnelles */
    @Column(name = "notes", length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospitalisation_id")
    private Hospitalisation hospitalisation;
}
