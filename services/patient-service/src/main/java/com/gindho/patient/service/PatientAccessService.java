package com.gindho.patient.service;

import com.gindho.patient.model.Patient;
import com.gindho.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j @Service @RequiredArgsConstructor
public class PatientAccessService {
    private final PatientRepository patientRepository;

    public void assertPatientAccess(Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        boolean isPatient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
        if (isPatient) {
            Long userId = getCurrentUserId(null);
            Optional<Patient> patOpt = patientRepository.findByUserIdAndActifTrue(userId);
            if (patOpt.isEmpty() || !patOpt.get().getId().equals(patientId)) {
                throw new AccessDeniedException("Patient access denied");
            }
        }
    }

    public Long getCurrentUserId(Jwt jwt) {
        if (jwt != null && jwt.getSubject() != null && jwt.getSubject().startsWith("local-token-")) {
            return Long.valueOf(jwt.getSubject().substring("local-token-".length()));
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        return Long.valueOf(auth.getName());
    }

    private Long getUserIdFromEmail(String email) {
        // In real implementation, call identity service or user repository
        return null;
    }
}