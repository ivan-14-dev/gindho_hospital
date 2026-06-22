package com.gindho.service;
import com.gindho.dto.PresenceDto;
import com.gindho.model.*;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class PresenceService {
    private final PresenceRepository presenceRepository;
    private final PersonnelRepository personnelRepository;

    @Transactional
    public PresenceDto pointer(Long personnelId) {
        Personnel p = personnelRepository.findById(personnelId).orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        Presence pres = Presence.builder().date(java.time.LocalDate.now()).present(true)
            .heureArrivee(java.time.LocalTime.now()).personnel(p).build();
        return toDto(presenceRepository.save(pres));
    }

    public List<PresenceDto> listByPersonnel(Long personnelId) {
        return presenceRepository.findByPersonnelIdOrderByDateDesc(personnelId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private PresenceDto toDto(Presence pr) {
        String nom = pr.getPersonnel() != null ? pr.getPersonnel().getPrenom() + " " + pr.getPersonnel().getNom() : "";
        return PresenceDto.builder().id(pr.getId()).date(pr.getDate()).heureArrivee(pr.getHeureArrivee())
            .heureDepart(pr.getHeureDepart()).present(pr.isPresent())
            .personnelId(pr.getPersonnel() != null ? pr.getPersonnel().getId() : null).personnelNom(nom.trim()).build();
    }
}