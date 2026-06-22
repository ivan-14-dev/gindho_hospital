package com.gindho.service;
import com.gindho.dto.CongeDto;
import com.gindho.model.*;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class CongeService {
    private final CongeRepository congeRepository;
    private final PersonnelRepository personnelRepository;

    @Transactional
    public CongeDto create(CongeDto dto) {
        Personnel p = personnelRepository.findById(dto.getPersonnelId()).orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        Conge c = Conge.builder().dateDebut(dto.getDateDebut()).dateFin(dto.getDateFin())
            .typeConge(dto.getTypeConge()).motif(dto.getMotif()).valide(false).personnel(p).build();
        return toDto(congeRepository.save(c));
    }

    @Transactional
    public CongeDto valider(Long id) {
        Conge c = congeRepository.findById(id).orElseThrow(() -> new RuntimeException("Congé non trouvé"));
        c.setValide(true);
        return toDto(congeRepository.save(c));
    }

    private CongeDto toDto(Conge c) {
        String nom = c.getPersonnel() != null ? c.getPersonnel().getPrenom() + " " + c.getPersonnel().getNom() : "";
        return CongeDto.builder().id(c.getId()).dateDebut(c.getDateDebut()).dateFin(c.getDateFin())
            .typeConge(c.getTypeConge()).motif(c.getMotif()).valide(c.isValide())
            .personnelId(c.getPersonnel() != null ? c.getPersonnel().getId() : null).personnelNom(nom.trim()).build();
    }
}