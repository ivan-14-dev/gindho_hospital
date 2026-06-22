package com.gindho.service;

import com.gindho.dto.GardeDto;
import com.gindho.model.*;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GardeService {
    private final GardeRepository gardeRepository;
    private final MedecinRepository medecinRepository;

    public List<GardeDto> listByMedecin(Long medecinId) {
        return gardeRepository.findByMedecinIdOrderByDateDebutDesc(medecinId)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public GardeDto create(GardeDto dto) {
        Medecin medecin = medecinRepository.findById(dto.getMedecinId())
            .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        Garde garde = Garde.builder().typeGarde(dto.getTypeGarde()).dateDebut(dto.getDateDebut())
            .dateFin(dto.getDateFin()).confirmee(false).medecin(medecin).build();
        return toDto(gardeRepository.save(garde));
    }

    @Transactional
    public GardeDto confirmer(Long id) {
        Garde garde = gardeRepository.findById(id).orElseThrow(() -> new RuntimeException("Garde non trouvée"));
        garde.setConfirmee(true);
        return toDto(gardeRepository.save(garde));
    }

    private GardeDto toDto(Garde g) {
        String nom = g.getMedecin() != null && g.getMedecin().getUser() != null
            ? g.getMedecin().getUser().getPrenom() + " " + g.getMedecin().getUser().getNom() : "";
        return GardeDto.builder().id(g.getId()).typeGarde(g.getTypeGarde()).dateDebut(g.getDateDebut())
            .dateFin(g.getDateFin()).confirmee(g.isConfirmee())
            .medecinId(g.getMedecin() != null ? g.getMedecin().getId() : null).medecinNom(nom.trim()).build();
    }
}