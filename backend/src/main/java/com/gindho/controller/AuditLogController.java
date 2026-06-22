package com.gindho.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gindho.model.AuditLog;
import com.gindho.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> list(
            @RequestParam(name = "entite", required = false) String entite,
            @RequestParam(name = "entiteId", required = false) String entiteId
    ) {
        // Audit: afficher les plus récents d'abord
        java.util.Comparator<AuditLog> byCreeLeDesc =
                java.util.Comparator.comparing(AuditLog::getCreeLe, java.util.Comparator.nullsLast(java.lang.Comparable::compareTo)).reversed();

        if (entite != null && !entite.isBlank()) {
            return auditLogRepository.findByEntite(entite.trim())
                    .stream()
                    .sorted(byCreeLeDesc)
                    .toList();
        }
        if (entiteId != null && !entiteId.isBlank()) {
            return auditLogRepository.findByEntiteId(entiteId.trim())
                    .stream()
                    .sorted(byCreeLeDesc)
                    .toList();
        }
        return auditLogRepository.findAll()
                .stream()
                .sorted(byCreeLeDesc)
                .toList();
    }
}
