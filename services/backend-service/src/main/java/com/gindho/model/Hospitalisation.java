package com.gindho.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hospitalisations", indexes = {
        @Index(name = "idx_hosp_statut", columnList = "statut"),
        @Index(name = "idx_hosp_date_admission", columnList = "date_admission")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Hospitalisation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lit_id", nullable = false)
    private Lit lit;

    @Column(name = "date_admission", nullable = false)
    private LocalDateTime dateAdmission;

    @Column(name = "date_sortie")
    private LocalDateTime dateSortie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutHospitalisation statut = StatutHospitalisation.EN_COURS;

    @Column(name = "motif_admission", length = 1000)
    private String motifAdmission;

    public enum StatutHospitalisation {
        EN_COURS,
        SORTIE
    }
}
