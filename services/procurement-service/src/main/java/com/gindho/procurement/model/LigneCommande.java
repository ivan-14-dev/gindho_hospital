package com.gindho.procurement.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name="lignes_commande") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LigneCommande extends BaseEntity {
    private Long bonCommandeId; private Long produitId; private String libelle;
    private int quantiteCommandee; private int quantiteRecue;
    private BigDecimal prixUnitaire; private BigDecimal tva;
}