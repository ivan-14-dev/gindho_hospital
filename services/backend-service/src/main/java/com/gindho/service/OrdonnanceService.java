package com.gindho.service;

import com.gindho.dto.PrescriptionDto;
import com.gindho.model.DossierMedical;
import com.gindho.model.Medecin;
import com.gindho.model.Ordonnance;
import com.gindho.model.Patient;
import com.gindho.repository.DossierMedicalRepository;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.OrdonnanceRepository;
import com.gindho.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final PatientAccessService patientAccessService;

    // ===== READ (ownership filtering) =====

    public Page<PrescriptionDto> getAllPrescriptions(Pageable pageable) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            return ordonnanceRepository.findAll(pageable).map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long patientId = getCurrentPatientIdOrThrow();
            return ordonnanceRepository.findByDossierMedical_Patient_Id(patientId, pageable)
                    .map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long medecinId = getCurrentMedecinIdOrThrow();
            return ordonnanceRepository.findByDossierMedical_RendezVous_Medecin_Id(medecinId, pageable)
                    .map(this::convertToDto);
        }

        // fallback: keep current behavior for other roles
        return ordonnanceRepository.findAll(pageable).map(this::convertToDto);
    }

    public Page<PrescriptionDto> getPrescriptionsByPatient(Long patientId, Pageable pageable) {
        patientAccessService.assertPatientAccess(patientId);
        return ordonnanceRepository.findByDossierMedical_Patient_Id(patientId, pageable)
                .map(this::convertToDto);
    }

    public PrescriptionDto getPrescriptionById(Long id) {
        Ordonnance ordonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription non trouvée"));

        assertCanAccessOrdonnance(ordonnance);
        return convertToDto(ordonnance);
    }

    // ===== WRITE =====

    public PrescriptionDto createPrescription(PrescriptionDto dto) {
        DossierMedical dossier = null;

        if (dto.getDossierMedicalId() != null) {
            dossier = dossierMedicalRepository.findById(dto.getDossierMedicalId()).orElse(null);
        }

        // If no dossier provided, try to find existing one for the patient, or create one
        if (dossier == null && dto.getPatientId() != null) {
            dossier = dossierMedicalRepository.findByPatientId(dto.getPatientId()).orElse(null);
        }

        if (dossier == null && dto.getPatientId() != null) {
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
            dossier = DossierMedical.builder()
                    .patient(patient)
                    .build();
            dossier = dossierMedicalRepository.save(dossier);
        }

        if (dossier == null) {
            throw new RuntimeException("Dossier médical non trouvé et impossible à créer (patientId requis)");
        }

        // Sécurité: si le créateur est MEDECIN, il faut un rendez-vous rattaché (sinon on ne peut pas rattacher la prescription à son périmètre)
        Authentication auth = getAuth();
        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            if (dossier.getRendezVous() == null || dossier.getRendezVous().getMedecin() == null) {
                throw new AccessDeniedException("Impossible de créer une prescription: dossier médical sans rendez-vous rattaché");
            }
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            if (!currentMedecinId.equals(dossier.getRendezVous().getMedecin().getId())) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire du rendez-vous)");
            }
        } else if (hasAnyRole(auth, "ROLE_PATIENT")) {
            if (dto.getPatientId() == null || !dto.getPatientId().equals(getCurrentPatientIdOrThrow())) {
                throw new AccessDeniedException("Accès refusé (patient non propriétaire)");
            }
        } else {
            // ADMIN/SUPER_ADMIN: ok
        }

        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setMedicament(dto.getMedicament());
        ordonnance.setPosologie(dto.getPosologie());
        ordonnance.setDuree(dto.getDuree());
        ordonnance.setDateEmission(dto.getDateEmission() != null ? dto.getDateEmission() : LocalDate.now());
        ordonnance.setDossierMedical(dossier);

        return convertToDto(ordonnanceRepository.save(ordonnance));
    }

    public PrescriptionDto updatePrescription(Long id, PrescriptionDto dto) {
        Ordonnance ordonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription non trouvée"));

        assertCanAccessOrdonnance(ordonnance);

        if (dto.getMedicament() != null) ordonnance.setMedicament(dto.getMedicament());
        if (dto.getPosologie() != null) ordonnance.setPosologie(dto.getPosologie());
        if (dto.getDuree() != null) ordonnance.setDuree(dto.getDuree());
        if (dto.getDateEmission() != null) ordonnance.setDateEmission(dto.getDateEmission());

        if (dto.getDossierMedicalId() != null) {
            DossierMedical dossier = dossierMedicalRepository.findById(dto.getDossierMedicalId())
                    .orElseThrow(() -> new RuntimeException("Dossier médical non trouvé"));
            ordonnance.setDossierMedical(dossier);

            // On revalide l’accès après changement de dossier
            assertCanAccessOrdonnance(ordonnance);
        }

        return convertToDto(ordonnanceRepository.save(ordonnance));
    }

    public void deletePrescription(Long id) {
        Ordonnance ordonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription non trouvée"));

        assertCanAccessOrdonnance(ordonnance);
        ordonnanceRepository.deleteById(id);
    }

    // ===== Helpers =====

    private void assertCanAccessOrdonnance(Ordonnance ordonnance) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            return;
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long currentPatientId = getCurrentPatientIdOrThrow();
            Patient p = ordonnance.getDossierMedical() != null ? ordonnance.getDossierMedical().getPatient() : null;
            if (p == null || p.getId() == null || !currentPatientId.equals(p.getId())) {
                throw new AccessDeniedException("Accès refusé (patient non propriétaire)");
            }
            return;
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            Medecin m = null;
            if (ordonnance.getDossierMedical() != null
                    && ordonnance.getDossierMedical().getRendezVous() != null
                    && ordonnance.getDossierMedical().getRendezVous().getMedecin() != null) {
                m = ordonnance.getDossierMedical().getRendezVous().getMedecin();
            }

            if (m == null || m.getId() == null || !currentMedecinId.equals(m.getId())) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire du rendez-vous)");
            }
            return;
        }

        // fallback compatible
        if (ordonnance.getDossierMedical() != null
                && ordonnance.getDossierMedical().getPatient() != null
                && ordonnance.getDossierMedical().getPatient().getId() != null) {
            patientAccessService.assertPatientAccess(ordonnance.getDossierMedical().getPatient().getId());
        }
    }

    private Authentication getAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Non authentifié");
        }
        return auth;
    }

    private boolean hasAnyRole(Authentication auth, String... roles) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a != null && a.getAuthority() != null && java.util.Set.of(roles).contains(a.getAuthority()));
    }

    private Long getCurrentPatientIdOrThrow() {
        Authentication auth = getAuth();
        String email = auth.getName();
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Patient non trouvé pour votre compte"));
        return patient.getId();
    }

    private Long getCurrentMedecinIdOrThrow() {
        Authentication auth = getAuth();
        String email = auth.getName();
        Medecin medecin = medecinRepository.findByUserEmail(email)
                .orElseThrow(() -> new AccessDeniedException("Médecin non trouvé pour votre compte"));
        return medecin.getId();
    }

    private PrescriptionDto convertToDto(Ordonnance ordonnance) {
        PrescriptionDto dto = new PrescriptionDto();
        dto.setId(ordonnance.getId());
        dto.setMedicament(ordonnance.getMedicament());
        dto.setPosologie(ordonnance.getPosologie());
        dto.setDuree(ordonnance.getDuree());
        dto.setDateEmission(ordonnance.getDateEmission());

        if (ordonnance.getDossierMedical() != null) {
            dto.setDossierMedicalId(ordonnance.getDossierMedical().getId());

            if (ordonnance.getDossierMedical().getPatient() != null) {
                Patient p = ordonnance.getDossierMedical().getPatient();
                dto.setPatientId(p.getId());
                if (p.getUser() != null) {
                    dto.setPatientNom(p.getUser().getPrenom() + " " + p.getUser().getNom());
                }
            }

            if (ordonnance.getDossierMedical().getRendezVous() != null
                    && ordonnance.getDossierMedical().getRendezVous().getMedecin() != null) {
                Medecin m = ordonnance.getDossierMedical().getRendezVous().getMedecin();
                if (m.getUser() != null) {
                    dto.setMedecinNom(m.getUser().getPrenom() + " " + m.getUser().getNom());
                }
            }
        }

        return dto;
    }
}
