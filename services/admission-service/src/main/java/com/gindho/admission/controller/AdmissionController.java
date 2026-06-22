package com.gindho.admission.controller;

import com.gindho.admission.dto.HospitalisationDto;
import com.gindho.admission.service.HospitalisationService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdmissionController {
    private final HospitalisationService hospitalisationService;

    @GetMapping("/api/admissions/patient/{patientId}")
    public ResponseEntity<ApiResponse<?>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(hospitalisationService.findByPatient(patientId)));
    }

    @GetMapping("/api/admissions/en-cours")
    public ResponseEntity<ApiResponse<?>> enCours() {
        return ResponseEntity.ok(ApiResponse.of(hospitalisationService.findEnCours()));
    }

    @GetMapping("/api/admissions")
    public ResponseEntity<ApiResponse<?>> all() {
        return ResponseEntity.ok(ApiResponse.of(hospitalisationService.findAll()));
    }

    @PostMapping("/api/admissions")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody HospitalisationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(hospitalisationService.create(dto)));
    }

    @PutMapping("/api/admissions/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @RequestBody HospitalisationDto dto) {
        return ResponseEntity.ok(ApiResponse.of(hospitalisationService.update(id, dto)));
    }

    @DeleteMapping("/api/admissions/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hospitalisationService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Hospitalisation supprimée", null));
    }

    @PutMapping("/api/admissions/{id}/discharge")
    public ResponseEntity<ApiResponse<Void>> discharge(@PathVariable Long id) {
        hospitalisationService.sortir(id);
        return ResponseEntity.ok(ApiResponse.ok("Patient discharged", null));
    }

    @PatchMapping("/api/admissions/{id}/rapport-sortie")
    public ResponseEntity<ApiResponse<?>> rapportSortie(@PathVariable Long id, @RequestBody HospitalisationDto dto) {
        return ResponseEntity.ok(ApiResponse.of(hospitalisationService.rapportSortie(id, dto)));
    }
}
