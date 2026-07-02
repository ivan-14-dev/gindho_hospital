package com.gindho.teleconsultation.controller;

import com.gindho.base.ApiResponse;
import com.gindho.teleconsultation.model.Teleconsultation;
import com.gindho.teleconsultation.service.TeleconsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeleconsultationController {

    private final TeleconsultationService teleconsultationService;

    @GetMapping("/api/teleconsultations")
    public ResponseEntity<ApiResponse<List<Teleconsultation>>> list() {
        return ResponseEntity.ok(ApiResponse.okList(teleconsultationService.findAll()));
    }

    @PostMapping("/api/teleconsultations")
    public ResponseEntity<ApiResponse<Teleconsultation>> create(@Valid @RequestBody Teleconsultation teleconsultation) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Téléconsultation créée", teleconsultationService.create(teleconsultation)));
    }

    @PatchMapping("/api/teleconsultations/{id}/statut")
    public ResponseEntity<ApiResponse<Teleconsultation>> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour",
                teleconsultationService.updateStatut(id, statut)));
    }
}