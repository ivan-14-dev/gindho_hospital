package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Administration d'un médicament à un patient (par un infirmier).
 */
@Entity
@Table(name = "administrations_medicaments", indexes = {
    @Index(name = "idx_admin_med_patient", columnList = "patient_id"),
    @Index(name = "idx_admin_med_date", columnList = "date_administration")
})
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdministrationMedicament extends BaseEntity {

    @Column(name = "medicament", nullable = false, length = 255)
    private String medicament;

    @Column(name = "posologie", nullable = false, length = 255)
    private String posologie;

    @Column(name = "voie_administration", length = 100)
    private String voieAdministration;

    @Column(name = "date_administration", nullable = false)
    private LocalDateTime dateAdministration;

    @Column(name = "administre", nullable = false)
    private boolean administre = false;

    @Column(name = "notes", length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospitalisation_id")
    private Hospitalisation hospitalisation;
}
