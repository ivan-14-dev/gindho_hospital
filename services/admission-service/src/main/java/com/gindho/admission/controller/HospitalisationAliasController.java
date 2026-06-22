package com.gindho.admission.controller;

import com.gindho.admission.dto.HospitalisationDto;
import com.gindho.admission.model.Hospitalisation;
import com.gindho.admission.service.HospitalisationService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class HospitalisationAliasController {

    private final HospitalisationService hospitalisationService;

    @GetMapping("/api/hospitalisations")
    public ResponseEntity<ApiResponse<List<HospitalisationDto>>> list() {
        return ResponseEntity.ok(ApiResponse.okList(hospitalisationService.findAll()
                .stream().map(hospitalisationService::toDto).toList()));
    }

    @GetMapping("/api/hospitalisations/en-cours")
    public ResponseEntity<ApiResponse<List<HospitalisationDto>>> enCours() {
        return ResponseEntity.ok(ApiResponse.okList(hospitalisationService.findEnCours()
                .stream().map(hospitalisationService::toDto).toList()));
    }

    @GetMapping("/api/hospitalisations/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<HospitalisationDto>>> byPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.okList(hospitalisationService.findByPatient(patientId)
                .stream().map(hospitalisationService::toDto).toList()));
    }

    @PostMapping("/api/hospitalisations")
    public ResponseEntity<ApiResponse<HospitalisationDto>> create(@Valid @RequestBody HospitalisationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Hospitalisation créée", hospitalisationService.create(dto)));
    }

    @PutMapping("/api/hospitalisations/{id}")
    public ResponseEntity<ApiResponse<HospitalisationDto>> update(
            @PathVariable Long id, @RequestBody HospitalisationDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Hospitalisation mise à jour", hospitalisationService.update(id, dto)));
    }

    @DeleteMapping("/api/hospitalisations/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hospitalisationService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Hospitalisation supprimée").build());
    }

    @PatchMapping("/api/hospitalisations/{id}/rapport-sortie")
    public ResponseEntity<ApiResponse<HospitalisationDto>> rapportSortie(
            @PathVariable Long id, @RequestBody HospitalisationDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Rapport de sortie enregistré",
                hospitalisationService.rapportSortie(id, dto)));
    }
}
