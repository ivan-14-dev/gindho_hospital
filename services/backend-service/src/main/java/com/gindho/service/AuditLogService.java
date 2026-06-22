package com.gindho.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.gindho.model.AuditLog;
import com.gindho.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String action, String entite, String entiteId, String details, String ipAdresse) {
        try {
            AuditLog log = new AuditLog();
            log.setAction(action);
            log.setEntite(entite);
            log.setEntiteId(entiteId);
            log.setDetails(details);
            log.setIpAdresse(ipAdresse);
            auditLogRepository.save(log);
        } catch (Exception ex) {
            // Ne jamais remonter l'exception (et éviter de casser d'autres threads / logs)
            System.err.println("AuditLog async failed: " + ex.getClass().getName() + " - " + ex.getMessage());
        }
    }
}
