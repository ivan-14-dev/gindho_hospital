package com.gindho.procurement.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="fournisseurs") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Fournisseur extends BaseEntity {
    @Column(unique=true) private String code; private String nom; private String contact;
    private String email; private String telephone; private String adresse; private String categorie;
    private boolean actif = true;
}