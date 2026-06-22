package com.gindho.event.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evenements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evenement extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String titre;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, length = 100)
    private String typeEvenement;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    @Column(nullable = false)
    private boolean valide = false;
}
