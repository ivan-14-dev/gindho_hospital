package com.gindho.service;
import com.gindho.dto.EquipementDto;
import com.gindho.model.Equipement;
import com.gindho.repository.EquipementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class EquipementService {
    private final EquipementRepository equipementRepository;

    public List<EquipementDto> list() {
        return equipementRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public EquipementDto create(EquipementDto dto) {
        Equipement e = Equipement.builder().nom(dto.getNom()).modele(dto.getModele())
            .numeroSerie(dto.getNumeroSerie()).statut(dto.getStatut() != null ? dto.getStatut() : "OPERATIONNEL")
            .dateAchat(dto.getDateAchat()).dateDerniereMaintenance(dto.getDateDerniereMaintenance())
            .dateProchaineMaintenance(dto.getDateProchaineMaintenance()).build();
        return toDto(equipementRepository.save(e));
    }

    @Transactional
    public EquipementDto updateStatut(Long id, String statut) {
        Equipement e = equipementRepository.findById(id).orElseThrow();
        e.setStatut(statut);
        return toDto(equipementRepository.save(e));
    }

    private EquipementDto toDto(Equipement e) {
        return EquipementDto.builder().id(e.getId()).nom(e.getNom()).modele(e.getModele())
            .numeroSerie(e.getNumeroSerie()).statut(e.getStatut()).dateAchat(e.getDateAchat())
            .dateDerniereMaintenance(e.getDateDerniereMaintenance()).dateProchaineMaintenance(e.getDateProchaineMaintenance()).build();
    }
}