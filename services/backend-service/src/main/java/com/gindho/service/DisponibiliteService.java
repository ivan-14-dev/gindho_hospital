package com.gindho.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.dto.DisponibiliteDto;
import com.gindho.model.Disponibilite;
import com.gindho.model.Medecin;
import com.gindho.repository.DisponibiliteRepository;
import com.gindho.repository.MedecinRepository;

@Service
@Transactional
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final MedecinRepository medecinRepository;

    public DisponibiliteService(DisponibiliteRepository disponibiliteRepository, MedecinRepository medecinRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
        this.medecinRepository = medecinRepository;
    }

    public List<DisponibiliteDto> listByMedecin(Long medecinId) {
        if (medecinId == null) throw new IllegalArgumentException("medecinId manquant");
        return disponibiliteRepository.findByMedecinId(medecinId).stream()
                .map(this::convertToDto)
                .toList();
    }

    public DisponibiliteDto create(DisponibiliteDto dto) {
        if (dto == null) throw new IllegalArgumentException("payload manquant");
        if (dto.getMedecinId() == null) throw new IllegalArgumentException("medecinId manquant");
        if (dto.getJour() == null) throw new IllegalArgumentException("jour manquant");
        if (dto.getHeureDebut() == null) throw new IllegalArgumentException("heureDebut manquante");
        if (dto.getHeureFin() == null) throw new IllegalArgumentException("heureFin manquante");

        Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        if (!dto.getHeureFin().isAfter(dto.getHeureDebut())) {
            throw new RuntimeException("heureFin doit être après heureDebut");
        }

        Disponibilite entity = new Disponibilite();
        entity.setMedecin(medecin);
        entity.setJour(dto.getJour());
        entity.setHeureDebut(dto.getHeureDebut());
        entity.setHeureFin(dto.getHeureFin());
        entity.setActif(dto.isActif());

        Disponibilite saved = disponibiliteRepository.save(entity);
        return convertToDto(saved);
    }

    public DisponibiliteDto update(Long id, DisponibiliteDto dto) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (dto == null) throw new IllegalArgumentException("payload manquant");

        Disponibilite entity = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));

        if (dto.getMedecinId() != null) {
            Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                    .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
            entity.setMedecin(medecin);
        }
        if (dto.getJour() != null) entity.setJour(dto.getJour());
        if (dto.getHeureDebut() != null) entity.setHeureDebut(dto.getHeureDebut());
        if (dto.getHeureFin() != null) entity.setHeureFin(dto.getHeureFin());
        entity.setActif(dto.isActif());

        if (entity.getHeureDebut() == null || entity.getHeureFin() == null || !entity.getHeureFin().isAfter(entity.getHeureDebut())) {
            throw new RuntimeException("heureFin doit être après heureDebut");
        }

        Disponibilite saved = disponibiliteRepository.save(entity);
        return convertToDto(saved);
    }

    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (!disponibiliteRepository.existsById(id)) throw new RuntimeException("Disponibilité non trouvée");
        disponibiliteRepository.deleteById(id);
    }

    private DisponibiliteDto convertToDto(Disponibilite d) {
        if (d == null) return null;

        DisponibiliteDto dto = new DisponibiliteDto();
        dto.setId(d.getId());
        dto.setMedecinId(d.getMedecin() != null ? d.getMedecin().getId() : null);
        dto.setJour(d.getJour());
        dto.setHeureDebut(d.getHeureDebut());
        dto.setHeureFin(d.getHeureFin());
        dto.setActif(d.isActif());
        return dto;
    }
}
