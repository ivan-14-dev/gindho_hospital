package com.gindho.hr.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "employes") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Employe extends BaseEntity {
    private String matricule; private String nom; private String prenom;
    private String email; private String telephone;
    @Enumerated(EnumType.STRING) private Fonction fonction;
    private String specialite; private String departement;
    private LocalDate dateEmbauche; private boolean actif = true;
    @Column(name = "user_id") private Long userId;
    public enum Fonction { MEDECIN, INFIRMIER, ADMINISTRATIF, LABORANTIN, PHARMACIEN, TECHNIQUE, AUTRE }
}
