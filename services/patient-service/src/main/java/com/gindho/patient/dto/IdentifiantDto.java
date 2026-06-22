package com.gindho.patient.dto;

import lombok.*;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentifiantDto {
    private Long id;
    private Long patientId;

    @NotBlank(message = "Le type d'identifiant est obligatoire")
    @Size(max = 80, message = "Le type d'identifiant doit contenir au maximum 80 caractères")
    private String typeIdentifiant;

    @NotBlank(message = "La valeur de l'identifiant est obligatoire")
    @Size(max = 100, message = "La valeur doit contenir au maximum 100 caractères")
    private String valeur;
}
