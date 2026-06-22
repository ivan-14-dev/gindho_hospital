package com.gindho.service;

import com.gindho.dto.AnalyseDto;
import com.gindho.model.Analyse;
import com.gindho.model.Medecin;
import com.gindho.model.Patient;
import com.gindho.repository.AnalyseRepository;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalyseService {

    private final AnalyseRepository analyseRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final PatientAccessService patientAccessService;
    private final PatientMaladieService patientMaladieService;
    private final MailService mailService;

    // ===== READ (ownership filtering) =====

    public Page<AnalyseDto> getAllAnalyses(Pageable pageable) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            return analyseRepository.findAll(pageable).map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long patientId = getCurrentPatientIdOrThrow();
            return analyseRepository.findByPatientId(patientId, pageable).map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long medecinId = getCurrentMedecinIdOrThrow();
            return analyseRepository.findByMedecinId(medecinId, pageable).map(this::convertToDto);
        }

        // Other roles: keep current behavior (RBAC at endpoint level already allows it)
        return analyseRepository.findAll(pageable).map(this::convertToDto);
    }

    public Page<AnalyseDto> getAnalysesByPatient(Long patientId, Pageable pageable) {
        patientAccessService.assertPatientAccess(patientId);
        return analyseRepository.findByPatientId(patientId, pageable).map(this::convertToDto);
    }

    public Page<AnalyseDto> getAnalysesByMedecin(Long medecinId, Pageable pageable) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            return analyseRepository.findByMedecinId(medecinId, pageable).map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            if (!currentMedecinId.equals(medecinId)) {
                throw new AccessDeniedException("Accès refusé (médecin non propriétaire)");
            }
            return analyseRepository.findByMedecinId(medecinId, pageable).map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long patientId = getCurrentPatientIdOrThrow();
            return analyseRepository
                    .findByPatientIdAndMedecinId(patientId, medecinId, pageable)
                    .map(this::convertToDto);
        }

        // Other roles: keep current behavior
        return analyseRepository.findByMedecinId(medecinId, pageable).map(this::convertToDto);
    }

    public Page<AnalyseDto> getUrgentAnalyses(Pageable pageable) {
        Authentication auth = getAuth();

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            return analyseRepository.findUrgentAnalyses(pageable).map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long medecinId = getCurrentMedecinIdOrThrow();
            return analyseRepository.findByMedecinIdAndUrgentTrue(medecinId, pageable).map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long patientId = getCurrentPatientIdOrThrow();
            return analyseRepository.findByPatientIdAndUrgentTrue(patientId, pageable).map(this::convertToDto);
        }

        // Other roles: keep current behavior
        return analyseRepository.findUrgentAnalyses(pageable).map(this::convertToDto);
    }

    public Page<AnalyseDto> searchAnalyses(String type, Pageable pageable) {
        Authentication auth = getAuth();
        String safeType = type == null ? "" : type;

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            return analyseRepository.findByTypeAnalyseContainingIgnoreCase(safeType, pageable).map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long medecinId = getCurrentMedecinIdOrThrow();
            return analyseRepository
                    .findByMedecinIdAndTypeAnalyseContainingIgnoreCase(medecinId, safeType, pageable)
                    .map(this::convertToDto);
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long patientId = getCurrentPatientIdOrThrow();
            return analyseRepository
                    .findByPatientIdAndTypeAnalyseContainingIgnoreCase(patientId, safeType, pageable)
                    .map(this::convertToDto);
        }

        // Other roles: keep current behavior
        return analyseRepository.findByTypeAnalyseContainingIgnoreCase(safeType, pageable).map(this::convertToDto);
    }

    public AnalyseDto getAnalyseById(Long id) {
        Authentication auth = getAuth();
        Analyse analyse = analyseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analyse non trouvée"));

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            return convertToDto(analyse);
        }

        if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long patientId = getCurrentPatientIdOrThrow();
            if (analyse.getPatient() == null || analyse.getPatient().getId() == null || !patientId.equals(analyse.getPatient().getId())) {
                throw new AccessDeniedException("Accès refusé (analyse non liée au patient)");
            }
            return convertToDto(analyse);
        }

        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long medecinId = getCurrentMedecinIdOrThrow();
            if (analyse.getMedecin() == null || analyse.getMedecin().getId() == null || !medecinId.equals(analyse.getMedecin().getId())) {
                throw new AccessDeniedException("Accès refusé (analyse non liée au médecin)");
            }
            return convertToDto(analyse);
        }

        // Other roles: fallback to existing patient-based check for compatibility
        if (analyse.getPatient() != null && analyse.getPatient().getId() != null) {
            patientAccessService.assertPatientAccess(analyse.getPatient().getId());
        }

        return convertToDto(analyse);
    }

    // ===== WRITE (kept as-is except for strict read safety) =====

    public AnalyseDto createAnalyse(AnalyseDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        Analyse analyse = Analyse.builder()
                .typeAnalyse(dto.getTypeAnalyse())
                .resultat(dto.getResultat())
                .observation(dto.getObservation())
                .dateAnalyse(dto.getDateAnalyse() != null ? dto.getDateAnalyse() : LocalDateTime.now())
                .urgent(dto.getUrgent() != null ? dto.getUrgent() : false)
                .patient(patient)
                .medecin(medecin)
                .build();

        Analyse saved = analyseRepository.save(analyse);

        if (saved.getPatient() != null && saved.getPatient().getId() != null) {
            patientMaladieService.recomputeForPatient(saved.getPatient().getId());
        }

        boolean isResultatBlank = saved.getResultat() == null || saved.getResultat().isBlank();
        if (!isResultatBlank) {
            mailService.sendAnalysesDisponibles(saved);
        }

        return convertToDto(saved);
    }

    public AnalyseDto updateAnalyse(Long id, AnalyseDto dto) {
        Authentication auth = getAuth();
        Analyse analyse = analyseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analyse non trouvée"));

        // En lecture/filtrage strict : si MEDECIN/PATIENT, on limite au propriétaire de l'objet
        if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            if (analyse.getMedecin() == null || analyse.getMedecin().getId() == null || !currentMedecinId.equals(analyse.getMedecin().getId())) {
                throw new AccessDeniedException("Accès refusé (analyse non liée au médecin)");
            }
        } else if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long currentPatientId = getCurrentPatientIdOrThrow();
            if (analyse.getPatient() == null || analyse.getPatient().getId() == null || !currentPatientId.equals(analyse.getPatient().getId())) {
                throw new AccessDeniedException("Accès refusé (analyse non liée au patient)");
            }
        } else if (analyse.getPatient() != null && analyse.getPatient().getId() != null) {
            // fallback compatibilité
            patientAccessService.assertPatientAccess(analyse.getPatient().getId());
        }

        boolean wasResultatBlank = analyse.getResultat() == null || analyse.getResultat().isBlank();

        if (dto.getTypeAnalyse() != null) analyse.setTypeAnalyse(dto.getTypeAnalyse());
        if (dto.getResultat() != null) analyse.setResultat(dto.getResultat());
        if (dto.getObservation() != null) analyse.setObservation(dto.getObservation());
        if (dto.getDateAnalyse() != null) analyse.setDateAnalyse(dto.getDateAnalyse());
        if (dto.getUrgent() != null) analyse.setUrgent(dto.getUrgent());

        if (dto.getPatientId() != null) {
            patientAccessService.assertPatientAccess(dto.getPatientId());
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
            analyse.setPatient(patient);
        }

        if (dto.getMedecinId() != null) {
            Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
            analyse.setMedecin(medecin);
        }

        Analyse saved = analyseRepository.save(analyse);

        if (saved.getPatient() != null && saved.getPatient().getId() != null) {
            patientMaladieService.recomputeForPatient(saved.getPatient().getId());
        }

        boolean isResultatBlank = saved.getResultat() == null || saved.getResultat().isBlank();
        if (wasResultatBlank && !isResultatBlank) {
            mailService.sendAnalysesDisponibles(saved);
        }

        return convertToDto(saved);
    }

    public void deleteAnalyse(Long id) {
        Authentication auth = getAuth();
        Analyse analyse = analyseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analyse non trouvée"));

        if (hasAnyRole(auth, "ROLE_ADMIN", "ROLE_SUPER_ADMIN")) {
            // ok
        } else if (hasAnyRole(auth, "ROLE_MEDECIN")) {
            Long currentMedecinId = getCurrentMedecinIdOrThrow();
            if (analyse.getMedecin() == null || analyse.getMedecin().getId() == null || !currentMedecinId.equals(analyse.getMedecin().getId())) {
                throw new AccessDeniedException("Accès refusé (analyse non liée au médecin)");
            }
        } else if (hasAnyRole(auth, "ROLE_PATIENT")) {
            Long currentPatientId = getCurrentPatientIdOrThrow();
            if (analyse.getPatient() == null || analyse.getPatient().getId() == null || !currentPatientId.equals(analyse.getPatient().getId())) {
                throw new AccessDeniedException("Accès refusé (analyse non liée au patient)");
            }
        } else if (analyse.getPatient() != null && analyse.getPatient().getId() != null) {
            patientAccessService.assertPatientAccess(analyse.getPatient().getId());
        }

        Long patientId = null;
        if (analyse.getPatient() != null && analyse.getPatient().getId() != null) {
            patientId = analyse.getPatient().getId();
            patientMaladieService.recomputeForPatient(patientId);
        }

        analyseRepository.deleteById(id);
    }

    // ===== Helpers =====

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

    private AnalyseDto convertToDto(Analyse analyse) {
        return AnalyseDto.builder()
                .id(analyse.getId())
                .typeAnalyse(analyse.getTypeAnalyse())
                .resultat(analyse.getResultat())
                .observation(analyse.getObservation())
                .dateAnalyse(analyse.getDateAnalyse())
                .urgent(analyse.isUrgent())
                .patientId(analyse.getPatient() != null ? analyse.getPatient().getId() : null)
                .medecinId(analyse.getMedecin() != null ? analyse.getMedecin().getId() : null)
                .patientNom(analyse.getPatient() != null && analyse.getPatient().getUser() != null ?
                        analyse.getPatient().getUser().getPrenom() + " " + analyse.getPatient().getUser().getNom() : "")
                .medecinNom(analyse.getMedecin() != null && analyse.getMedecin().getUser() != null ?
                        analyse.getMedecin().getUser().getPrenom() + " " + analyse.getMedecin().getUser().getNom() : "")
                .build();
    }
}
