package com.gindho.service;

import com.gindho.dto.MedicamentDto;
import com.gindho.model.Medicament;
import com.gindho.repository.MedicamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicamentService {

    private final MedicamentRepository medicamentRepository;

    public MedicamentDto create(MedicamentDto dto) {
        if (dto == null) throw new IllegalArgumentException("payload manquant");
        if (dto.getNom() == null || dto.getNom().isBlank()) throw new IllegalArgumentException("nom manquant");

        Medicament medicament = new Medicament();
        medicament.setNom(dto.getNom().trim());
        medicament.setDescription(dto.getDescription());
        medicament.setActif(dto.isActif());

        Medicament saved = medicamentRepository.save(medicament);
        return convertToDto(saved);
    }

    public List<MedicamentDto> list() {
        return medicamentRepository.findByActifTrue().stream().map(this::convertToDto).toList();
    }

    public MedicamentDto update(Long id, MedicamentDto dto) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (dto == null) throw new IllegalArgumentException("payload manquant");

        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicament non trouvée"));

        if (dto.getNom() != null && !dto.getNom().isBlank()) {
            medicament.setNom(dto.getNom().trim());
        }
        if (dto.getDescription() != null) {
            medicament.setDescription(dto.getDescription());
        }
        medicament.setActif(dto.isActif());

        Medicament saved = medicamentRepository.save(medicament);
        return convertToDto(saved);
    }

    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        // MVP: delete hard
        medicamentRepository.deleteById(id);
    }

    private MedicamentDto convertToDto(Medicament medicament) {
        if (medicament == null) return null;

        MedicamentDto dto = new MedicamentDto();
        dto.setId(medicament.getId());
        dto.setNom(medicament.getNom());
        dto.setDescription(medicament.getDescription());
        dto.setActif(medicament.isActif());
        return dto;
    }
}
