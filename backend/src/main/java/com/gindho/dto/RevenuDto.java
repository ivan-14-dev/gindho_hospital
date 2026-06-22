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
public class RevenuDto {
    private Long id;
    private BigDecimal montant;
    private String description;
    private String typeRevenu;
    private String notes;
    private LocalDateTime dateRevenu;
    private Long patientId;
    private Long medecinId;
    private String patientNom;
    private String medecinNom;
}
