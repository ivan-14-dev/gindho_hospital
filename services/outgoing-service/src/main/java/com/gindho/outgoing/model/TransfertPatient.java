package com.gindho.outgoing.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferts_patients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransfertPatient extends BaseEntity {
    private Long patientId;
    private String patientNom;
    private String hopitalSource;
    private String hopitalDestination;

    @Column(columnDefinition = "TEXT")
    private String motifTransfert;

    @Enumerated(EnumType.STRING)
    private StatutTransfert statut = StatutTransfert.EN_ATTENTE;

    private Long medecinReferentId;
    private LocalDateTime dateDemande;
    private LocalDateTime dateValidation;
    private Long valideParId;

    @Column(columnDefinition = "TEXT")
    private String notesTransport;

    private boolean urgence;

    public enum StatutTransfert {
        EN_ATTENTE, VALIDE, REFUSE, EFFECTUE, ANNULE
    }
}