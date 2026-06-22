package com.gindho.emr.controller;

import com.gindho.base.ApiResponse;
import com.gindho.emr.dto.ConsultationDto;
import com.gindho.emr.dto.DiagnosticDto;
import com.gindho.emr.dto.ObservationDto;
import com.gindho.emr.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConsultationController {
    private final ConsultationService consultationService;

    @GetMapping("/consultations/patient/{patientId}")
    public ResponseEntity<ApiResponse<Object>> byPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(consultationService.findByPatient(patientId)));
    }

    @GetMapping("/consultations/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<Object>> byMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(ApiResponse.of(consultationService.findByMedecin(medecinId)));
    }

    @PostMapping("/consultations")
    public ResponseEntity<ApiResponse<ConsultationDto>> create(@RequestBody ConsultationDto dto) {
        return ResponseEntity.ok(ApiResponse.of(consultationService.create(dto)));
    }

    @PostMapping("/diagnostics")
    public ResponseEntity<ApiResponse<DiagnosticDto>> createDiagnostic(@RequestBody DiagnosticDto dto) {
        return ResponseEntity.ok(ApiResponse.of(consultationService.createDiagnostic(dto)));
    }

    @GetMapping("/consultations/{consultationId}/diagnostics")
    public ResponseEntity<ApiResponse<Object>> diagnostics(@PathVariable Long consultationId) {
        return ResponseEntity.ok(ApiResponse.of(consultationService.diagnostics(consultationId)));
    }

    @PostMapping("/observations")
    public ResponseEntity<ApiResponse<ObservationDto>> createObservation(@RequestBody ObservationDto dto) {
        return ResponseEntity.ok(ApiResponse.of(consultationService.createObservation(dto)));
    }

    @GetMapping("/consultations/{consultationId}/observations")
    public ResponseEntity<ApiResponse<Object>> observations(@PathVariable Long consultationId) {
        return ResponseEntity.ok(ApiResponse.of(consultationService.observations(consultationId)));
    }
}
