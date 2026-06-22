package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "rondes_medicales")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class RondeMedicale extends BaseEntity {
    @Column(nullable=false, length=100) private String typeRonde; // MATIN, SOIR, SPECIALISEE
    @Column(nullable=false) private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    @Column(nullable=false) private boolean validee = false;
    @Column(length=2000) private String compteRendu;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="medecin_id", nullable=false)
    private Medecin medecin;
}