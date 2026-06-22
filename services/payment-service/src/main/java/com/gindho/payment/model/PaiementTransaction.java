package com.gindho.payment.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="payment_transactions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaiementTransaction extends BaseEntity {
    private Long patientId;
    private Long factureId; @Column(nullable=false) private BigDecimal montant;
    private LocalDateTime datePaiement; private String modePaiement; private String reference;
    @Enumerated(EnumType.STRING) private StatutTransaction statut;
    @Column(unique=true) private String codeTransaction; private String notes;
}