package com.gindho.surgery.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "interventions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class InterventionChirurgicale extends BaseEntity {
    private Long patientId; private Long medecinId; private String salle;
    private LocalDateTime dateProgrammee; private LocalDateTime dateDebut; private LocalDateTime dateFin;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(columnDefinition = "TEXT") private String compteRendu;
    @Enumerated(EnumType.STRING) private StatutIntervention statut = StatutIntervention.PROGRAMMEE;
    private String equipe; // JSON list of participant IDs
    public enum StatutIntervention { PROGRAMMEE, EN_COURS, TERMINEE, ANNULEE, REPORTEE }
}