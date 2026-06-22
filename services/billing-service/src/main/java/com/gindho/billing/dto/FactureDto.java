package com.gindho.billing.dto;
import lombok.*; import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FactureDto {
    private Long id; private String numeroFacture; private Long patientId;
    private BigDecimal montant; private BigDecimal montantPaye; private BigDecimal remise;
    private String statut; private LocalDateTime dateEmission; private LocalDate dateEcheance;
    private String description; private String notes;
}