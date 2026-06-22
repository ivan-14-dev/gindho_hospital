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
public class FactureDto {
    private Long id;

    private BigDecimal montantTotal;
    private BigDecimal montantPayee;

    private String description;
    private String statut;

    private LocalDateTime dateFacture;
    private LocalDateTime echeance;

    private Long patientId;
    private Long medecinId;

    private String patientNom;
    private String medecinNom;
}
