package com.gindho.patient.dto;

import lombok.*;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssurancePatientDto {
    private Long id;
    private Long patientId;

    @NotBlank(message = "La compagnie d'assurance est obligatoire")
    @Size(max = 100, message = "La compagnie doit contenir au maximum 100 caractères")
    private String compagnie;

    @NotBlank(message = "Le numéro de police est obligatoire")
    @Size(max = 100, message = "Le numéro de police doit contenir au maximum 100 caractères")
    private String numeroPolice;

    private LocalDate validiteDebut;
    private LocalDate validiteFin;

    @DecimalMin(value = "0.0", message = "La couverture doit être positive ou nulle")
    private Double couverture;

    private boolean actif;
}
