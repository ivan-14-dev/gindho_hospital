package com.gindho.pharmacy.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal;
@Entity @Table(name = "medicaments") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Medicament extends BaseEntity {
    @Column(unique = true) private String code; private String nom;
    private String forme; private String dosage; private String voieAdministration;
    private BigDecimal prixUnitaire; private int stockMinimum; private boolean actif = true;
}