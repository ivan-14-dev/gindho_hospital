package com.gindho.procurement.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;
@Entity @Table(name="bons_commande") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BonCommande extends BaseEntity {
    @Column(unique=true) private String code; private Long fournisseurId;
    private LocalDateTime dateCommande; private LocalDate dateLivraisonPrevue;
    @Enumerated(EnumType.STRING) private StatutCommande statut;
    private BigDecimal montantTotal; private Long validePar; private String notes;
}