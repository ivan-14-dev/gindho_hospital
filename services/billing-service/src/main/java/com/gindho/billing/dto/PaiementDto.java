package com.gindho.billing.dto;
import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaiementDto {
    private Long id; private Long factureId; private BigDecimal montant;
    private LocalDateTime datePaiement; private String modePaiement;
    private String reference; private String notes;
}