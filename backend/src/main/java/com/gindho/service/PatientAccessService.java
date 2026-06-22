package com.gindho.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.model.Medecin;
import com.gindho.model.Patient;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.PatientRepository;
import com.gindho.repository.RendezVousRepository;

@Service
@Transactional(readOnly = true)
public class PatientAccessService {

    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final RendezVousRepository rendezVousRepository;

    public PatientAccessService(
            PatientRepository patientRepository,
            MedecinRepository medecinRepository,
            RendezVousRepository rendezVousRepository
    ) {
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.rendezVousRepository = rendezVousRepository;
    }

    public void assertPatientAccess(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("patientId manquant");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || !auth.isAuthenticated()) {
            throw new RuntimeException("Non authentifié");
        }

        boolean isAdminRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_SUPER_ADMIN".equals(a.getAuthority()));
        if (isAdminRole) return;

        boolean isPatientRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
        if (isPatientRole) {
            enforcePatientOwnership(patientId, auth.getName());
            return;
        }

        boolean isMedecinRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MEDECIN".equals(a.getAuthority()));
        if (isMedecinRole) {
            enforceMedecinAffiliation(patientId, auth.getName());
            return;
        }

        // Other roles: keep compatibility for now (RBAC already gates endpoints).
    }

    /**
     * Strict variant used for RBAC dynamic conditions (scope/condition_type).
     * If the condition requires "patient ownership/assignment" but current role
     * is neither PATIENT nor MEDECIN (or ADMIN/SUPER_ADMIN), access is denied.
     */
    public void assertPatientAccessStrict(Long patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("patientId manquant");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || !auth.isAuthenticated()) {
            throw new RuntimeException("Non authentifié");
        }

        boolean isAdminRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_SUPER_ADMIN".equals(a.getAuthority()));
        if (isAdminRole) return;

        boolean isPatientRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PATIENT".equals(a.getAuthority()));
        if (isPatientRole) {
            enforcePatientOwnership(patientId, auth.getName());
            return;
        }

        boolean isMedecinRole = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MEDECIN".equals(a.getAuthority()));
        if (isMedecinRole) {
            enforceMedecinAffiliation(patientId, auth.getName());
            return;
        }

        // For dynamic "referent/assigned to patient" conditions, deny unsupported roles.
        throw new AccessDeniedException("Accès refusé (scope/condition patient non applicable à votre rôle)");
    }

    private void enforcePatientOwnership(Long patientId, String email) {
        Patient patientFromJwt = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé pour votre compte"));

        if (!patientId.equals(patientFromJwt.getId())) {
            throw new AccessDeniedException("Accès refusé au dossier patient");
        }
    }

    private void enforceMedecinAffiliation(Long patientId, String email) {
        Medecin medecin = medecinRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé pour votre compte"));

        boolean allowed = rendezVousRepository.existsByMedecinIdAndPatientId(medecin.getId(), patientId);
        if (!allowed) {
            throw new AccessDeniedException("Accès refusé au dossier patient (médecin non affilié)");
        }
    }
}
