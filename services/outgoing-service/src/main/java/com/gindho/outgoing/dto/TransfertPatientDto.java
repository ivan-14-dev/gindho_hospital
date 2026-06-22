package com.gindho.outgoing.dto;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransfertPatientDto {
    private Long id; private Long patientId; private String patientNom;
    private String hopitalSource; private String hopitalDestination; private String motifTransfert;
    private String statut; private Long medecinReferentId; private LocalDateTime dateDemande;
    private LocalDateTime dateValidation; private String notesTransport; private boolean urgence;
}
