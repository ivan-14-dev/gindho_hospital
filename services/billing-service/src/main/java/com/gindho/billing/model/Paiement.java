package com.gindho.billing.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDateTime;

@Entity @Table(name = "paiements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Paiement extends BaseEntity {
    private Long factureId;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private String modePaiement;
    private String reference;
    private String notes;
}