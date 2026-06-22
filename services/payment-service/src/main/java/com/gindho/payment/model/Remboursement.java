package com.gindho.payment.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="remboursements") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Remboursement extends BaseEntity {
    private Long transactionId; @Column(nullable=false) private BigDecimal montant;
    private String motif; private LocalDateTime dateRemboursement; private Long validePar;
    @Enumerated(EnumType.STRING) private StatutRemboursement statut;
}