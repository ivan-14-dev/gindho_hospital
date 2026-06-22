package com.gindho.service;
import com.gindho.dto.IncidentDto;
import com.gindho.model.Incident;
import com.gindho.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class IncidentService {
    private final IncidentRepository incidentRepository;

    public List<IncidentDto> listNonResolus() {
        return incidentRepository.findByResoluFalseOrderByDateDeclarationDesc().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public IncidentDto create(IncidentDto dto) {
        Incident i = Incident.builder().titre(dto.getTitre()).description(dto.getDescription())
            .typeIncident(dto.getTypeIncident()).gravite(dto.getGravite())
            .dateDeclaration(java.time.LocalDateTime.now()).resolu(false).build();
        return toDto(incidentRepository.save(i));
    }

    @Transactional
    public IncidentDto resoudre(Long id, String action) {
        Incident i = incidentRepository.findById(id).orElseThrow(() -> new RuntimeException("Incident non trouvé"));
        i.setResolu(true);
        if (action != null) i.setActionCorrective(action);
        return toDto(incidentRepository.save(i));
    }

    private IncidentDto toDto(Incident i) {
        return IncidentDto.builder().id(i.getId()).titre(i.getTitre()).description(i.getDescription())
            .typeIncident(i.getTypeIncident()).gravite(i.getGravite()).dateDeclaration(i.getDateDeclaration())
            .actionCorrective(i.getActionCorrective()).resolu(i.isResolu()).build();
    }
}