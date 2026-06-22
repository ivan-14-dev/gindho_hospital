package com.gindho.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.dto.AnalyseDto;
import com.gindho.dto.PatientDashboardDto;
import com.gindho.dto.PatientDto;
import com.gindho.dto.RendezVousDto;
import com.gindho.model.Patient;
import com.gindho.model.RendezVous;
import com.gindho.model.User;
import com.gindho.repository.AnalyseRepository;
import com.gindho.repository.PatientRepository;
import com.gindho.repository.RendezVousRepository;
import com.gindho.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final RendezVousRepository rendezVousRepository;
    private final AnalyseRepository analyseRepository;
    private final PatientAccessService patientAccessService;

    public PatientDto creer(PatientDto dto) {
        Patient patient = convertToEntity(dto);
        
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            patient.setUser(user);
        }
        
        patient = patientRepository.save(patient);
        return convertToDto(patient);
    }

    public Page<PatientDto> rechercher(String criteres, Pageable pageable) {
        if (criteres != null && !criteres.isEmpty()) {
            return patientRepository.findByNomContainingOrPrenomContainingOrNumeroPatientContaining(
                    criteres, pageable).map(this::convertToDto);
        }
        return patientRepository.findAll(pageable).map(this::convertToDto);
    }

    public PatientDto findById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        return convertToDto(patient);
    }

    public PatientDto mettreAJour(Long id, PatientDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        
        patient.setNumeroPatient(dto.getNumeroPatient());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setSexe(dto.getSexe());
        patient.setTelephone(dto.getTelephone());
        patient.setGroupeSanguin(dto.getGroupeSanguin());
        patient.setTaille(dto.getTaille());
        patient.setVille(dto.getVille());
        patient.setAntecedents(dto.getAntecedents());
        patient.setAllergies(dto.getAllergies());
        patient.setAdresse(dto.getAdresse());

        // Mettre à jour l'utilisateur lié (nom/prenom/email)
        // Important : sans ça, UI peut envoyer les champs mais DB ne change pas.
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            patient.setUser(user);
        }

        if (patient.getUser() != null) {
            if (dto.getNom() != null && !dto.getNom().isBlank()) {
                patient.getUser().setNom(dto.getNom().trim());
            }
            if (dto.getPrenom() != null && !dto.getPrenom().isBlank()) {
                patient.getUser().setPrenom(dto.getPrenom().trim());
            }
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                patient.getUser().setEmail(dto.getEmail().trim());
            }

            // Si email change, ça peut avoir des contraintes d'unicité côté DB.
            // On laisse l'exception remonter (et l'UI affichera le message).
        }
        
        patient = patientRepository.save(patient);
        return convertToDto(patient);
    }

    public PatientDto getDossier(Long patientId) {
        patientAccessService.assertPatientAccess(patientId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        return convertToDto(patient);
    }

    public Long getCurrentPatientIdFromJwt() {
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String email = auth.getName();
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé pour votre compte"));

        return patient.getId();
    }

    public void archiver(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        patientRepository.delete(patient);
    }

    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient non trouvé");
        }
        patientRepository.deleteById(id);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public PatientDashboardDto getDashboard(Long patientId) {
        patientAccessService.assertPatientAccess(patientId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        
        LocalDateTime now = LocalDateTime.now();
        List<RendezVousDto> rendezVous = rendezVousRepository.findByPatientIdWithPatientAndMedecin(patientId).stream()
                .map(this::convertRendezVousToDto)
                .collect(Collectors.toList());

        var analysesPage = analyseRepository.findByPatientId(patientId, Pageable.unpaged());
        List<AnalyseDto> analyses = analysesPage.getContent().stream()
                .map(this::convertAnalyseToDto)
                .collect(Collectors.toList());

        Long prochainsRendezVous = rendezVousRepository.countByPatientIdAndDateHeureDebutAfterAndStatutNot(
                patientId, now, RendezVous.StatutRDV.ANNULE);
        Long analysesEnAttente = analysesPage.getTotalElements();

        return PatientDashboardDto.builder()
                .patientId(patient.getId())
                .nom(patient.getUser() != null ? patient.getUser().getNom() : "")
                .prenom(patient.getUser() != null ? patient.getUser().getPrenom() : "")
                .prochainsRendezVous(prochainsRendezVous != null ? prochainsRendezVous : 0L)
                .analysesEnAttente(analysesEnAttente)
                .rendezVous(rendezVous)
                .analyses(analyses)
                .build();
    }

    private RendezVousDto convertRendezVousToDto(com.gindho.model.RendezVous r) {
        String medecinNom = "";
        if (r.getMedecin() != null && r.getMedecin().getUser() != null) {
            medecinNom = r.getMedecin().getUser().getPrenom() + " " + r.getMedecin().getUser().getNom();
        }
        return RendezVousDto.builder()
                .id(r.getId())
                .dateHeureDebut(r.getDateHeureDebut())
                .dateHeureFin(r.getDateHeureFin())
                .statut(r.getStatut())
                .medecinNom(medecinNom)
                .patientId(r.getPatient() != null ? r.getPatient().getId() : null)
                .build();
    }

    private AnalyseDto convertAnalyseToDto(com.gindho.model.Analyse a) {
        return AnalyseDto.builder()
                .id(a.getId())
                .dateAnalyse(a.getDateAnalyse())
                .patientId(a.getPatient() != null ? a.getPatient().getId() : null)
                .build();
    }

    private PatientDto convertToDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .numeroPatient(patient.getNumeroPatient())
                .dateNaissance(patient.getDateNaissance())
                .sexe(patient.getSexe())
                .telephone(patient.getTelephone())
                .groupeSanguin(patient.getGroupeSanguin())
                .taille(patient.getTaille())
                .ville(patient.getVille())
                .antecedents(patient.getAntecedents())
                .allergies(patient.getAllergies())
                .adresse(patient.getAdresse())
                .userId(patient.getUser() != null ? patient.getUser().getId() : null)
                .nom(patient.getUser() != null ? patient.getUser().getNom() : "")
                .prenom(patient.getUser() != null ? patient.getUser().getPrenom() : "")
                .email(patient.getUser() != null ? patient.getUser().getEmail() : "")
                .build();
    }

    private Patient convertToEntity(PatientDto dto) {
        return Patient.builder()
                .numeroPatient(dto.getNumeroPatient())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .telephone(dto.getTelephone())
                .groupeSanguin(dto.getGroupeSanguin())
                .taille(dto.getTaille())
                .ville(dto.getVille())
                .antecedents(dto.getAntecedents())
                .allergies(dto.getAllergies())
                .adresse(dto.getAdresse())
                .build();
    }
}
