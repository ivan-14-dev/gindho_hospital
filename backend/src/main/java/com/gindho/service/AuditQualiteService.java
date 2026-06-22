package com.gindho.service;
import com.gindho.dto.AuditQualiteDto;
import com.gindho.model.AuditQualite;
import com.gindho.repository.AuditQualiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class AuditQualiteService {
    private final AuditQualiteRepository auditQualiteRepository;

    public List<AuditQualiteDto> list() {
        return auditQualiteRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AuditQualiteDto create(AuditQualiteDto dto) {
        AuditQualite aq = AuditQualite.builder().titre(dto.getTitre()).description(dto.getDescription())
            .typeAudit(dto.getTypeAudit()).dateAudit(dto.getDateAudit()).score(dto.getScore())
            .recommandations(dto.getRecommandations()).build();
        return toDto(auditQualiteRepository.save(aq));
    }

    private AuditQualiteDto toDto(AuditQualite aq) {
        return AuditQualiteDto.builder().id(aq.getId()).titre(aq.getTitre()).description(aq.getDescription())
            .typeAudit(aq.getTypeAudit()).dateAudit(aq.getDateAudit()).score(aq.getScore())
            .recommandations(aq.getRecommandations()).build();
    }
}