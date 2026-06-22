package com.gindho.dto;

import com.gindho.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    private Long id;
    private String numeroPatient;
    private LocalDate dateNaissance;
    private Patient.Sexe sexe;
    private String telephone;
    private String groupeSanguin;

    private String taille;
    private String ville;
    private String antecedents;

    private String allergies;
    private String adresse;

    private Long userId;
    private String nom;
    private String prenom;
    private String email;
}
