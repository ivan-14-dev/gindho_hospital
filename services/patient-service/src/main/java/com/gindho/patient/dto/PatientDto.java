package com.gindho.patient.dto;

import lombok.*;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    private Long id;
    private String numeroPatient;
    private Long userId;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom doit contenir au maximum 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom doit contenir au maximum 100 caractères")
    private String prenom;

    @Email(message = "L'email est invalide")
    @Size(max = 254, message = "L'email doit contenir au maximum 254 caractères")
    private String email;

    @Size(max = 100, message = "Le numéro d'identification doit contenir au maximum 100 caractères")
    private String idNumber;

    private LocalDate dateNaissance;

    @Size(max = 10, message = "Le sexe doit contenir au maximum 10 caractères")
    private String sexe;

    @Size(max = 30, message = "Le téléphone doit contenir au maximum 30 caractères")
    private String telephone;

    @Size(max = 20, message = "Le groupe sanguin doit contenir au maximum 20 caractères")
    private String groupeSanguin;

    @Size(max = 20, message = "La taille doit contenir au maximum 20 caractères")
    private String taille;

    @Size(max = 100, message = "La ville doit contenir au maximum 100 caractères")
    private String ville;

    @Size(max = 2000, message = "Les antécédents doivent contenir au maximum 2000 caractères")
    private String antecedents;

    @Size(max = 1000, message = "Les allergies doivent contenir au maximum 1000 caractères")
    private String allergies;

    @Size(max = 1000, message = "L'adresse doit contenir au maximum 1000 caractères")
    private String adresse;

    private boolean actif;
}
