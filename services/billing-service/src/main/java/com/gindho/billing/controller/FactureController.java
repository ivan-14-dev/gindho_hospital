package com.gindho.billing.controller;

import com.gindho.billing.dto.FactureDto;
import com.gindho.billing.model.StatutFacture;
import com.gindho.billing.service.FactureService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    @GetMapping("/api/factures")
    public ResponseEntity<ApiResponse<Page<FactureDto>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(factureService.findAll(pageable)));
    }

    @GetMapping("/api/factures/{id}")
    public ResponseEntity<ApiResponse<FactureDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Facture récupérée", factureService.findById(id)));
    }

    @GetMapping("/api/factures/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<FactureDto>>> getByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) String statut) {
        StatutFacture st = null;
        if (statut != null && !statut.isBlank()) {
            st = StatutFacture.valueOf(statut.trim().toUpperCase(java.util.Locale.ROOT));
        }
        if (st != null) {
            return ResponseEntity.ok(ApiResponse.of(factureService.findByStatut(st)));
        }
        return ResponseEntity.ok(ApiResponse.okList(factureService.getByPatient(patientId)));
    }

    @PostMapping("/api/factures")
    public ResponseEntity<ApiResponse<FactureDto>> create(@Valid @RequestBody FactureDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Facture créée", factureService.create(dto)));
    }

    @PutMapping("/api/factures/{id}")
    public ResponseEntity<ApiResponse<FactureDto>> update(
            @PathVariable Long id,
            @RequestBody FactureDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Facture mise à jour", factureService.update(id, dto)));
    }
}
