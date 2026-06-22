package com.gindho.quality.controller;

import com.gindho.base.ApiResponse;
import com.gindho.quality.model.AuditQualite;
import com.gindho.quality.model.Incident;
import com.gindho.quality.service.QualityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QualityController {

    private final QualityService qualityService;

    // ===== AUDITS QUALITÉ =====
    @GetMapping("/api/qualite/audits")
    public ResponseEntity<ApiResponse<List<AuditQualite>>> listAudits() {
        return ResponseEntity.ok(ApiResponse.okList(qualityService.listAudits()));
    }

    @PostMapping("/api/qualite/audits")
    public ResponseEntity<ApiResponse<AuditQualite>> createAudit(@Valid @RequestBody AuditQualite audit) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Audit créé", qualityService.createAudit(audit)));
    }

    // ===== INCIDENTS =====
    @GetMapping("/api/incidents/non-resolus")
    public ResponseEntity<ApiResponse<List<Incident>>> listIncidentsNonResolus() {
        return ResponseEntity.ok(ApiResponse.okList(qualityService.listIncidentsNonResolus()));
    }

    @PostMapping("/api/incidents")
    public ResponseEntity<ApiResponse<Incident>> createIncident(@Valid @RequestBody Incident incident) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Incident signalé", qualityService.createIncident(incident)));
    }

    @PatchMapping("/api/incidents/{id}/resoudre")
    public ResponseEntity<ApiResponse<Incident>> resoudreIncident(
            @PathVariable Long id,
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(ApiResponse.ok("Incident résolu",
                qualityService.resoudreIncident(id, action)));
    }
}