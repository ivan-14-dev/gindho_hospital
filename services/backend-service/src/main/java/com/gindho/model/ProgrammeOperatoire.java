package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "bloc_operatoire")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class ProgrammeOperatoire extends BaseEntity {
    @Column(name="salle", nullable=false, length=50) private String salle;
    @Column(nullable=false) private LocalDateTime dateDebut;
    @Column(nullable=false) private LocalDateTime dateFin;
    @Column(length=500) private String intervention;
    @Column(nullable=false, length=50) private String statut; // PROGRAMME, EN_COURS, TERMINE, ANNULE
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id", nullable=false)
    private Patient patient;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="chirurgien_id", nullable=false)
    private Medecin chirurgien;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="anesthesiste_id")
    private Medecin anesthesiste;
}