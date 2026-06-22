package com.gindho.audit.controller;

import com.gindho.audit.dto.AuditQualiteDto;
import com.gindho.audit.dto.IncidentDto;
import com.gindho.audit.service.QualityService;
import com.gindho.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QualityController {
    private final QualityService qualityService;

    @GetMapping("/qualite/audits")
    public ResponseEntity<ApiResponse<Object>> listAudits() {
        return ResponseEntity.ok(ApiResponse.of(qualityService.listAudits()));
    }

    @PostMapping("/qualite/audits")
    public ResponseEntity<ApiResponse<AuditQualiteDto>> createAudit(@RequestBody AuditQualiteDto dto) {
        return ResponseEntity.ok(ApiResponse.of(qualityService.createAudit(dto)));
    }

    @GetMapping("/incidents/non-resolus")
    public ResponseEntity<ApiResponse<Object>> listIncidentsNonResolus() {
        return ResponseEntity.ok(ApiResponse.of(qualityService.listNonResolus()));
    }

    @PostMapping("/incidents")
    public ResponseEntity<ApiResponse<IncidentDto>> createIncident(@RequestBody IncidentDto dto) {
        return ResponseEntity.ok(ApiResponse.of(qualityService.createIncident(dto)));
    }

    @PatchMapping("/incidents/{id}/resoudre")
    public ResponseEntity<ApiResponse<IncidentDto>> resoudre(@PathVariable Long id, @RequestParam(required = false) String action) {
        return ResponseEntity.ok(ApiResponse.of(qualityService.resoudre(id, action)));
    }
}
