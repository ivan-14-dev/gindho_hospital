package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO "patient-safe" — ne contient QUE les informations
 * que le client est autorisé à voir.
 * Le masquage des données se fait côté BACKEND uniquement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientSafeDto {
    private Long id;
    private String numeroPatient;
    private String nom;
    private String prenom;
    private String sexe;
    private String age;         // Calculé côté serveur, pas la date brute
    private String telephone;
    private String email;
    private String groupeSanguin;
    // Pas d'adresse complète, pas de personneContact, pas de numéro de sécurité sociale
}
