package com.gindho.appointment.controller;

import com.gindho.appointment.dto.TeleconsultationDto;
import com.gindho.appointment.service.TeleconsultationService;
import com.gindho.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teleconsultations")
@RequiredArgsConstructor
public class TeleconsultationController {
    private final TeleconsultationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<TeleconsultationDto>> create(@RequestBody TeleconsultationDto dto) {
        return ResponseEntity.ok(ApiResponse.of(service.create(dto)));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<ApiResponse<TeleconsultationDto>> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        return ResponseEntity.ok(ApiResponse.of(service.updateStatut(id, statut)));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<Object>> byPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(service.findByPatient(patientId)));
    }

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<Object>> byMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(ApiResponse.of(service.findByMedecin(medecinId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Téléconsultation supprimée", null));
    }
}
