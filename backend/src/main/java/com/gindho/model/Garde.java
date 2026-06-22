package com.gindho.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "gardes")
@Data @Builder @EqualsAndHashCode(callSuper = true) @NoArgsConstructor @AllArgsConstructor
public class Garde extends BaseEntity {
    @Column(name = "type_garde", nullable = false, length = 50)
    private String typeGarde;
    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;
    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;
    @Column(nullable = false)
    private boolean confirmee = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;
}