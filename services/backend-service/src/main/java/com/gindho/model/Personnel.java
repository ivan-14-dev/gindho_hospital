package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "personnel")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class Personnel extends BaseEntity {
    @Column(nullable=false, length=255) private String nom;
    @Column(nullable=false, length=255) private String prenom;
    @Column(nullable=false, unique=true, length=255) private String email;
    @Column(nullable=false, length=100) private String telephone;
    @Column(nullable=false, length=100) private String poste;
    @Column(nullable=false, length=100) private String departement;
    @Column(name="date_embauche") private LocalDate dateEmbauche;
    @Column(name="salaire_base", precision=12, scale=2) private java.math.BigDecimal salaireBase;
    @Column(nullable=false) private boolean actif = true;
    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;
}