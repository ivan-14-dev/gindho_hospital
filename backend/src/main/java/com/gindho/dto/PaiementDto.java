package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaiementDto {
    private Long id;

    private BigDecimal montant;
    private String description;
    private String modePaiement;
    private String statut;

    /**
     * Idempotency key (anti double-commit).
     */
    private String transactionRef;

    private LocalDateTime datePaiement;

    private Long factureId;
    private Long patientId;
    private Long medecinId;

    private String patientNom;
    private String medecinNom;
}
