package com.gindho.round.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="comptes_rendus_ronde") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CompteRenduRonde extends BaseEntity {
    private Long rondeId;
    @Column(columnDefinition="TEXT") private String contenu;
    private Long validePar;
    @Enumerated(EnumType.STRING) private StatutCompteRendu statut;
}