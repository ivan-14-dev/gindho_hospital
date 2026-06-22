package com.gindho.prescription.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="medicaments_prescrits") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class MedicamentPrescrit extends BaseEntity {
    private Long ordonnanceId; private String medicamentCode; private String medicamentNom;
    private String posologie; private String forme; private String duree;
    @Column(columnDefinition="TEXT") private String instructions; private int quantite;
}