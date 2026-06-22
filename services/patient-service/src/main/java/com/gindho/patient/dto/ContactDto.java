package com.gindho.patient.dto;

import lombok.*;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    private Long id;
    private Long patientId;

    @NotBlank(message = "Le nom du contact est obligatoire")
    @Size(max = 100, message = "Le nom du contact doit contenir au maximum 100 caractères")
    private String nom;

    @Size(max = 30, message = "Le téléphone doit contenir au maximum 30 caractères")
    private String telephone;

    @Size(max = 50, message = "La relation doit contenir au maximum 50 caractères")
    private String relation;

    @Email(message = "L'email est invalide")
    @Size(max = 254, message = "L'email doit contenir au maximum 254 caractères")
    private String email;
}
