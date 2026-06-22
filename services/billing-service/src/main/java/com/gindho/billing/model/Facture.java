package com.gindho.billing.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;

@Entity @Table(name = "factures")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Facture extends BaseEntity {
    @Column(unique = true) private String numeroFacture;
    private Long patientId;
    private BigDecimal montant;
    private BigDecimal montantPaye;
    private BigDecimal remise;
    @Enumerated(EnumType.STRING) private StatutFacture statut = StatutFacture.EMISE;
    private LocalDateTime dateEmission;
    private LocalDate dateEcheance;
    @Column(columnDefinition = "TEXT") private String description;
    private String notes;
}