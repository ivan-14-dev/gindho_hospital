package com.gindho.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsQueryDto {

    @NotBlank(message = "metric manquant")
    private String metric;

    /**
     * Bornes optionnelles (format ISO yyyy-MM-dd).
     * - Si from = null => fenêtre par défaut côté service
     * - Si to = null => fenêtre jusqu'à "now" (ou jusqu'au bucket de fin par défaut)
     */
    private LocalDate from;

    /**
     * Voir {@link #from}.
     */
    private LocalDate to;
}
