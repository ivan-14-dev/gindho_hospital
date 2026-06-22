package com.gindho.insurance.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "assurances_patients", indexes = {
    @Index(name = "idx_assur_patient_id", columnList = "patient_id"),
    @Index(name = "idx_assur_numero_police", columnList = "numero_police", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssurancePatient extends BaseEntity {

    @Column(name = "compagnie", nullable = false, length = 255)
    private String compagnie;

    @Column(name = "numero_police", nullable = false, length = 100)
    private String numeroPolice;

    @Column(name = "type_couverture", nullable = false, length = 100)
    private String typeCouverture;

    @Column(name = "taux_prise_en_charge", nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxPriseEnCharge = BigDecimal.valueOf(80.0);

    @Column(name = "plafond_annuel", precision = 12, scale = 2)
    private BigDecimal plafondAnnuel;

    @Column(name = "montant_consomme", precision = 12, scale = 2)
    private BigDecimal montantConsomme = BigDecimal.ZERO;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;
}
