package com.gindho.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.dto.AnalyseDto;
import com.gindho.dto.ConsultationDossierDto;
import com.gindho.dto.DossierMedicalCompletDto;
import com.gindho.dto.PrescriptionDto;
import com.gindho.model.Analyse;
import com.gindho.model.DossierMedical;
import com.gindho.model.HospitalSettings;
import com.gindho.model.Medecin;
import com.gindho.model.Ordonnance;
import com.gindho.model.Patient;
import com.gindho.model.RendezVous;
import com.gindho.repository.AnalyseRepository;
import com.gindho.repository.DossierMedicalRepository;
import com.gindho.repository.HospitalSettingsRepository;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.OrdonnanceRepository;
import com.gindho.repository.PatientRepository;
import com.gindho.repository.RendezVousRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DossierMedicalService {

    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final MedecinRepository medecinRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final AnalyseRepository analyseRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final HospitalSettingsRepository hospitalSettingsRepository;

    public DossierMedicalCompletDto getDossierMedicalComplet(Long patientId) {
        enforceAccessIfNeeded(patientId);

        boolean hideInternalClinicalForPatient = isPatientJwtView();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        String nom = patient.getUser() != null ? patient.getUser().getNom() : "";
        String prenom = patient.getUser() != null ? patient.getUser().getPrenom() : "";

        Integer age = null;
        if (patient.getDateNaissance() != null) {
            age = Period.between(patient.getDateNaissance(), LocalDate.now()).getYears();
        }

        HospitalSettings settings = hospitalSettingsRepository.findFirstByOrderByIdAsc().orElse(null);
        String nomEtablissement = settings != null ? settings.getNomEtablissement() : null;
        String logoBase64 = settings != null ? settings.getLogoBase64() : null;

        List<RendezVous> rdvs = rendezVousRepository.findByPatientIdWithPatientAndMedecin(patientId);
        rdvs.sort(Comparator.comparing(RendezVous::getDateHeureDebut));

        List<ConsultationDossierDto> consultations = rdvs.stream()
                .map(rdv -> buildConsultation(patientId, rdv, hideInternalClinicalForPatient))
                .toList();

        return DossierMedicalCompletDto.builder()
                .patientId(patientId)
                .nom(nom)
                .prenom(prenom)
                .age(age)
                .sexe(patient.getSexe())
                .groupeSanguin(patient.getGroupeSanguin())
                .taille(patient.getTaille())
                .ville(patient.getVille())
                .antecedents(patient.getAntecedents())
                .nomEtablissement(nomEtablissement)
                .logoBase64(logoBase64)
                .consultations(consultations)
                .build();
    }

    private ConsultationDossierDto buildConsultation(
            Long patientId,
            RendezVous rdv,
            boolean hideInternalClinicalForPatient
    ) {
        DossierMedical dossier = dossierMedicalRepository.findByRendezVousId(rdv.getId()).orElse(null);

        List<AnalyseDto> analyses = analyseRepository
                .findByPatientIdAndDossierMedical_RendezVous_Id(patientId, rdv.getId())
                .stream()
                .map(this::convertAnalyseToDto)
                .toList();

        List<PrescriptionDto> prescriptions = ordonnanceRepository
                .findByDossierMedical_Patient_IdAndDossierMedical_RendezVous_Id(patientId, rdv.getId())
                .stream()
                .map(this::convertOrdonnanceToDto)
                .toList();

        String diagnostic = (!hideInternalClinicalForPatient && dossier != null) ? dossier.getDiagnostic() : null;
        String traitement = (!hideInternalClinicalForPatient && dossier != null) ? dossier.getTraitement() : null;
        String observations = (!hideInternalClinicalForPatient && dossier != null) ? dossier.getObservations() : null;

        return ConsultationDossierDto.builder()
                .rendezVousId(rdv.getId())
                .dateHeureDebut(rdv.getDateHeureDebut())
                .dateHeureFin(rdv.getDateHeureFin())
                .statut(rdv.getStatut())
                .motif(rdv.getMotif())
                .notes(rdv.getNotes())
                .dossierMedicalId(dossier != null ? dossier.getId() : null)
                .dateConsultation(dossier != null ? dossier.getDateConsultation() : null)
                .diagnostic(diagnostic)
                .traitement(traitement)
                .observations(observations)
                .analyses(analyses)
                .prescriptions(prescriptions)
                .build();
    }

    private AnalyseDto convertAnalyseToDto(Analyse a) {
        String patientNom = "";
        if (a.getPatient() != null && a.getPatient().getUser() != null) {
            patientNom = a.getPatient().getUser().getPrenom() + " " + a.getPatient().getUser().getNom();
        }

        String medecinNom = "";
        if (a.getMedecin() != null && a.getMedecin().getUser() != null) {
            medecinNom = a.getMedecin().getUser().getPrenom() + " " + a.getMedecin().getUser().getNom();
        }

        return AnalyseDto.builder()
                .id(a.getId())
                .typeAnalyse(a.getTypeAnalyse())
                .resultat(a.getResultat())
                .observation(a.getObservation())
                .dateAnalyse(a.getDateAnalyse())
                .urgent(a.isUrgent())
                .patientId(a.getPatient() != null ? a.getPatient().getId() : null)
                .medecinId(a.getMedecin() != null ? a.getMedecin().getId() : null)
                .patientNom(patientNom)
                .medecinNom(medecinNom)
                .build();
    }

    private PrescriptionDto convertOrdonnanceToDto(Ordonnance o) {
        Long patientId = null;
        String patientNom = "";
        String medecinNom = "";
        Long dossierMedicalId = null;

        if (o.getDossierMedical() != null) {
            dossierMedicalId = o.getDossierMedical().getId();
            if (o.getDossierMedical().getPatient() != null) {
                patientId = o.getDossierMedical().getPatient().getId();
                if (o.getDossierMedical().getPatient().getUser() != null) {
                    patientNom = o.getDossierMedical().getPatient().getUser().getPrenom()
                            + " " + o.getDossierMedical().getPatient().getUser().getNom();
                }
            }

            if (o.getDossierMedical().getRendezVous() != null
                    && o.getDossierMedical().getRendezVous().getMedecin() != null
                    && o.getDossierMedical().getRendezVous().getMedecin().getUser() != null) {
                medecinNom = o.getDossierMedical().getRendezVous().getMedecin().getUser().getPrenom()
                        + " " + o.getDossierMedical().getRendezVous().getMedecin().getUser().getNom();
            }
        }

        LocalDate dateEmission = o.getDateEmission();

        return PrescriptionDto.builder()
                .id(o.getId())
                .medicament(o.getMedicament())
                .posologie(o.getPosologie())
                .duree(o.getDuree())
                .dateEmission(dateEmission)
                .dossierMedicalId(dossierMedicalId)
                .patientId(patientId)
                .patientNom(patientNom)
                .medecinNom(medecinNom)
                .build();
    }

    private void enforceAccessIfNeeded(Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Non authentifié");
        }

        boolean isAdminRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_SUPER_ADMIN".equals(a.getAuthority()));
        if (isAdminRole) return;

        boolean isPatientRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
        if (isPatientRole) {
            enforcePatientOwnershipIfNeeded(patientId);
            return;
        }

        boolean isMedecinRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MEDECIN".equals(a.getAuthority()));
        if (isMedecinRole) {
            String email = auth.getName();

            Medecin medecin = medecinRepository.findByUserEmail(email)
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

            boolean allowed = rendezVousRepository.existsByMedecinIdAndPatientId(medecin.getId(), patientId);
            if (!allowed) {
                throw new AccessDeniedException("Accès refusé au dossier patient (médecin non affilié)");
            }
            return;
        }

        // Autres rôles: on laisse l’accès inchangé pour l’instant (compatibilité avec les matcher SecurityConfig)
    }

    private void enforcePatientOwnershipIfNeeded(Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Non authentifié");
        }

        boolean isPatientRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));

        if (!isPatientRole) return;

        String email = auth.getName();
        Patient patientFromJwt = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé pour votre compte"));

        if (!patientFromJwt.getId().equals(patientId)) {
            throw new RuntimeException("Accès refusé au dossier patient");
        }
    }

    private boolean isPatientJwtView() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
    }
}
