package com.gindho.audit.service;

import com.gindho.audit.dto.AuditQualiteDto;
import com.gindho.audit.dto.IncidentDto;
import com.gindho.audit.model.AuditQualite;
import com.gindho.audit.model.Incident;
import com.gindho.audit.repository.AuditQualiteRepository;
import com.gindho.audit.repository.IncidentRepository;
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

    public List<AuditQualiteDto> listAudits() {
        return auditRepository.findAll().stream().map(this::toAuditDto).toList();
    }

    @Transactional
    public AuditQualiteDto createAudit(AuditQualiteDto dto) {
        AuditQualite audit = new AuditQualite();
        audit.setTitre(dto.getTitre());
        audit.setDomaine(dto.getDomaine());
        audit.setDescription(dto.getDescription());
        audit.setAuditeur(dto.getAuditeur());
        audit.setDateAudit(dto.getDateAudit());
        audit.setResultat(dto.getResultat());
        return toAuditDto(auditRepository.save(audit));
    }

    public List<IncidentDto> listNonResolus() {
        return incidentRepository.findByResoluFalseOrderByDateDeclarationDesc().stream().map(this::toIncidentDto).toList();
    }

    @Transactional
    public IncidentDto createIncident(IncidentDto dto) {
        Incident incident = new Incident();
        incident.setTitre(dto.getTitre());
        incident.setDescription(dto.getDescription());
        incident.setNiveauGravite(dto.getNiveauGravite());
        incident.setDeclarePar(dto.getDeclarePar());
        incident.setDateDeclaration(dto.getDateDeclaration() == null ? LocalDateTime.now() : dto.getDateDeclaration());
        return toIncidentDto(incidentRepository.save(incident));
    }

    @Transactional
    public IncidentDto resoudre(Long id, String action) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident introuvable"));
        incident.setResolu(true);
        incident.setDateResolution(LocalDateTime.now());
        incident.setActionCorrective(action);
        return toIncidentDto(incidentRepository.save(incident));
    }

    private AuditQualiteDto toAuditDto(AuditQualite audit) {
        AuditQualiteDto dto = new AuditQualiteDto();
        dto.setId(audit.getId());
        dto.setTitre(audit.getTitre());
        dto.setDomaine(audit.getDomaine());
        dto.setDescription(audit.getDescription());
        dto.setAuditeur(audit.getAuditeur());
        dto.setDateAudit(audit.getDateAudit());
        dto.setResultat(audit.getResultat());
        return dto;
    }

    private IncidentDto toIncidentDto(Incident incident) {
        IncidentDto dto = new IncidentDto();
        dto.setId(incident.getId());
        dto.setTitre(incident.getTitre());
        dto.setDescription(incident.getDescription());
        dto.setNiveauGravite(incident.getNiveauGravite());
        dto.setDeclarePar(incident.getDeclarePar());
        dto.setDateDeclaration(incident.getDateDeclaration());
        dto.setResolu(incident.isResolu());
        dto.setDateResolution(incident.getDateResolution());
        dto.setActionCorrective(incident.getActionCorrective());
        return dto;
    }
}
