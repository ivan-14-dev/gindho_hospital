package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "evenements")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class Evenement extends BaseEntity {
    @Column(nullable=false, length=255) private String titre;
    @Column(length=2000) private String description;
    @Column(nullable=false, length=100) private String typeEvenement; // REUNION, FORMATION, CAMPAGNE, AUDIT
    @Column(nullable=false) private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    @Column(nullable=false) private boolean valide = false;
}