package com.gindho.dto;

import com.gindho.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DossierMedicalCompletDto {
    private Long patientId;
    private String nom;
    private String prenom;

    // infos patient (pour l’en-tête du dossier)
    private Integer age;
    private Patient.Sexe sexe;
    private String groupeSanguin;
    private String taille;
    private String ville;
    private String antecedents;

    // branding hôpital/clinique
    private String nomEtablissement;
    private String logoBase64;

    // consultations = rendez-vous (ordonnés chronologiquement) + informations du dossier associé
    private List<ConsultationDossierDto> consultations;
}
