package com.gindho.pharmacy.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name = "lots_medicaments") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LotMedicament extends BaseEntity {
    private Long medicamentId; private String numeroLot;
    private LocalDate dateFabrication; private LocalDate datePeremption;
    private int quantiteRecue; private int quantiteRestante; private BigDecimal prixAchat;
}