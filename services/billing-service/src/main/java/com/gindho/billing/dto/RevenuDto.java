package com.gindho.billing.dto;
import lombok.*; import java.math.BigDecimal; import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RevenuDto {
    private Long id; private LocalDate date; private BigDecimal montantTotal;
    private int consultations; private int hospitalisations;
    private int analyses; private int pharmacie; private int autres;
}