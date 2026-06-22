package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDate;
@Entity @Table(name = "conges")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class Conge extends BaseEntity {
    @Column(nullable=false) private LocalDate dateDebut;
    @Column(nullable=false) private LocalDate dateFin;
    @Column(nullable=false, length=50) private String typeConge;
    @Column(length=500) private String motif;
    @Column(nullable=false) private boolean valide = false;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="personnel_id", nullable=false)
    private Personnel personnel;
}