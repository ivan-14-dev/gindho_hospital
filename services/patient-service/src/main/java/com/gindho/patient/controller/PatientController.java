package com.gindho.patient.controller;

import com.gindho.base.ApiResponse;
import com.gindho.patient.dto.PatientDashboardDto;
import com.gindho.patient.dto.PatientDossierResponse;
import com.gindho.patient.dto.PatientDto;
import com.gindho.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN','NURSE','RECEPTION')")
    public ResponseEntity<ApiResponse<Page<PatientDto>>> listPatients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long utilisateurId,
            Pageable pageable
    ) {
        log.debug("Listing patients - search: {}, utilisateurId: {}, page: {}", search, utilisateurId, pageable);
        if (utilisateurId != null) {
            return patientService.getPatientByUserId(utilisateurId)
                    .map(p -> {
                        Page<PatientDto> single = new org.springframework.data.domain.PageImpl<>(List.of(p), pageable, 1);
                        return ResponseEntity.ok(ApiResponse.ok("Patient récupéré par utilisateurId", single));
                    })
                    .orElseGet(() -> ResponseEntity.ok(ApiResponse.ok("Aucun patient trouvé", Page.empty(pageable))));
        }
        return ResponseEntity.ok(ApiResponse.ok("Patients récupérés", patientService.listPatients(search, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN','NURSE','RECEPTION')")
    public ResponseEntity<ApiResponse<PatientDto>> getPatient(@PathVariable Long id) {
        return patientService.getPatient(id)
                .map(p -> ResponseEntity.ok(ApiResponse.ok("Patient récupéré", p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Patient introuvable")));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','RECEPTION')")
    public ResponseEntity<ApiResponse<PatientDto>> createPatient(@Valid @RequestBody PatientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Patient créé", patientService.createPatient(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','RECEPTION')")
    public ResponseEntity<ApiResponse<PatientDto>> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Patient mis à jour", patientService.updatePatient(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Patient supprimé").build());
    }

    @GetMapping("/{id}/dossier")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN','NURSE')")
    public ResponseEntity<ApiResponse<PatientDossierResponse>> getDossier(@PathVariable Long id) {
        PatientDossierResponse dossier = patientService.getDossier(id);
        return ResponseEntity.ok(ApiResponse.ok("Dossier récupéré", dossier));
    }

    @GetMapping("/{id}/dossier-complet")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN')")
    public ResponseEntity<ApiResponse<PatientDossierResponse>> getDossierComplet(@PathVariable Long id) {
        return patientService.getDossierComplet(id)
                .map(d -> ResponseEntity.ok(ApiResponse.ok("Dossier complet récupéré", d)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Dossier introuvable")));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PatientDto>> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.ok(ApiResponse.error("Non authentifié"));
        }
        Long userId = resolveUserId(jwt);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error("Utilisateur non trouvé"));
        }
        return patientService.getPatientByUserId(userId)
                .map(p -> ResponseEntity.ok(ApiResponse.ok("Profil récupéré", p)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Profil patient introuvable")));
    }

    @GetMapping("/{id}/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN')")
    public ResponseEntity<ApiResponse<PatientDashboardDto>> getPatientDashboard(@PathVariable Long id) {
        return patientService.getPatientDashboard(id)
                .map(d -> ResponseEntity.ok(ApiResponse.ok("Dashboard récupéré", d)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Dashboard introuvable")));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PatientDashboardDto>> getStats() {
        return ResponseEntity.ok(ApiResponse.ok("Statistiques", patientService.getGlobalStats()));
    }

    private Long resolveUserId(Jwt jwt) {
        try {
            String sub = jwt.getSubject();
            if (sub != null && sub.startsWith("local-token-")) {
                sub = sub.substring("local-token-".length());
            }
            return sub != null ? Long.valueOf(sub) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}