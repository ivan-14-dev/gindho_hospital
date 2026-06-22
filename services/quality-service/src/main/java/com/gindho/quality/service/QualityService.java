package com.gindho.quality.service;

import com.gindho.quality.model.AuditQualite;
import com.gindho.quality.model.Incident;
import com.gindho.quality.repository.AuditQualiteRepository;
import com.gindho.quality.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QualityService {

    private final AuditQualiteRepository auditRepository;
    private final IncidentRepository incidentRepository;

    // ===== AUDITS =====
    public List<AuditQualite> listAudits() {
        return auditRepository.findAll();
    }

    @Transactional
    public AuditQualite createAudit(AuditQualite audit) {
        if (audit.getDateAudit() == null) {
            audit.setDateAudit(LocalDateTime.now());
        }
        if (audit.getStatut() == null) {
            audit.setStatut("PLANIFIE");
        }
        return auditRepository.save(audit);
    }

    // ===== INCIDENTS =====
    public List<Incident> listIncidentsNonResolus() {
        return incidentRepository.findByStatutNotOrderByDateDeclarationDesc("RESOLU");
    }

    @Transactional
    public Incident createIncident(Incident incident) {
        if (incident.getDateDeclaration() == null) {
            incident.setDateDeclaration(LocalDateTime.now());
        }
        if (incident.getStatut() == null) {
            incident.setStatut("SIGNALE");
        }
        return incidentRepository.save(incident);
    }

    @Transactional
    public Incident resoudreIncident(Long id, String action) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé: " + id));
        incident.setStatut("RESOLU");
        incident.setDateResolution(LocalDateTime.now());
        if (action != null) {
            incident.setActionCorrective(action);
        }
        return incidentRepository.save(incident);
    }
}