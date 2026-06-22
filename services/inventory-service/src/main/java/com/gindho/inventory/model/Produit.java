package com.gindho.inventory.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name = "produits") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Produit extends BaseEntity {
    private String code; private String nom; private String categorie;
    private int quantiteStock; private int seuilAlerte;
    private BigDecimal prixUnitaire; private String unite; private boolean actif = true;
}