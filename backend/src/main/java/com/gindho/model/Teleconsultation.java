package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "teleconsultations")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class Teleconsultation extends BaseEntity {
    @Column(nullable=false) private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    @Column(nullable=false, length=50) private String statut; // PLANIFIEE, EN_COURS, TERMINEE, ANNULEE
    @Column(length=500) private String lienVideo;
    @Column(length=2000) private String notes;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id", nullable=false)
    private Patient patient;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="medecin_id", nullable=false)
    private Medecin medecin;
}