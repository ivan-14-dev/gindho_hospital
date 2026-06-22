package com.gindho.laboratory.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "analyses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Analyse extends BaseEntity {
    private Long patientId;
    private Long medecinId;
    private String typeAnalyse;
    private String code;
    @Column(columnDefinition = "TEXT") private String description;
    private LocalDateTime datePrescription;
    private LocalDateTime dateResultat;
    @Column(columnDefinition = "TEXT") private String resultat;
    @Enumerated(EnumType.STRING) private StatutAnalyse statut = StatutAnalyse.EN_ATTENTE;
    private boolean urgent;

    public enum StatutAnalyse { EN_ATTENTE, EN_COURS, TERMINE, VALIDE, ANNULE }
}