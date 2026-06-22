package com.gindho.emergency.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "emergency_triage")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmergencyTriage extends BaseEntity {
    private Long patientId;
    private Long medecinId;
    @Enumerated(EnumType.STRING) private TriageLevel niveauTriage;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(columnDefinition = "TEXT") private String priseEnCharge;
    private LocalDateTime dateArrivee;
    @Enumerated(EnumType.STRING) private StatutUrgence statut = StatutUrgence.EN_ATTENTE;

    public enum TriageLevel { NIVEAU_1, NIVEAU_2, NIVEAU_3, NIVEAU_4, NIVEAU_5 }
    public enum StatutUrgence { EN_ATTENTE, EN_CONSULTATION, PRISE_EN_CHARGE, SORTI, HOSPITALISE }
}