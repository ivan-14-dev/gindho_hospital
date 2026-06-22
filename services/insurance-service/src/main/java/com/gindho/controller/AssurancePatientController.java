package com.gindho.insurance.controller;

import com.gindho.insurance.dto.AssurancePatientDto;
import com.gindho.insurance.service.AssurancePatientService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AssurancePatientController {

    private final AssurancePatientService service;

    @GetMapping("/api/assurances/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<AssurancePatientDto>>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.okList(service.findByPatient(patientId)));
    }

    @GetMapping("/api/assurances/patient/{patientId}/actives")
    public ResponseEntity<ApiResponse<List<AssurancePatientDto>>> findActiveByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.okList(service.findActiveByPatient(patientId)));
    }

    @GetMapping("/api/assurances/{id}")
    public ResponseEntity<ApiResponse<AssurancePatientDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Assurance récupérée", service.findById(id)));
    }

    @GetMapping("/api/assurances/recherche")
    public ResponseEntity<ApiResponse<List<AssurancePatientDto>>> search(
            @RequestParam String compagnie,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.okList(service.searchByCompagnie(compagnie, pageable)));
    }

    @PostMapping("/api/assurances")
    public ResponseEntity<ApiResponse<AssurancePatientDto>> create(
            @Valid @RequestBody AssurancePatientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Assurance créée", service.create(dto)));
    }

    @PutMapping("/api/assurances/{id}")
    public ResponseEntity<ApiResponse<AssurancePatientDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody AssurancePatientDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Assurance mise à jour", service.update(id, dto)));
    }

    @DeleteMapping("/api/assurances/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Assurance supprimée", null));
    }
}
