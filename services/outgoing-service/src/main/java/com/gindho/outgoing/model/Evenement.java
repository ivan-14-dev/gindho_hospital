package com.gindho.outgoing.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "evenements_publics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Evenement extends BaseEntity {
    @Column(nullable = false, length = 200) private String titre;
    @Column(columnDefinition = "TEXT") private String description;
    @Enumerated(EnumType.STRING) private TypeEvenement type;
    private LocalDateTime dateDebut; private LocalDateTime dateFin;
    @Column(length = 500) private String lieu;
    private String organisateur;
    @Enumerated(EnumType.STRING) private StatutEvenement statut = StatutEvenement.PLANIFIE;
    @Column(columnDefinition = "TEXT") private String contenuPublication;
    private boolean publique; private String imageUrl; private String lienInscription;
    public enum TypeEvenement { DON_SANG, CAMPAGNE_SENSIBILISATION, FORMATION, CONFERENCE, DEPISTAGE_GRATUIT, AUTRE }
    public enum StatutEvenement { PLANIFIE, EN_COURS, TERMINE, ANNULE }
}
