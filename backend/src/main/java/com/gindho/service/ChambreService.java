package com.gindho.service;

import com.gindho.dto.ChambreDto;
import com.gindho.model.Chambre;
import com.gindho.repository.ChambreRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChambreService {

    private final ChambreRepository chambreRepository;

    public ChambreService(ChambreRepository chambreRepository) {
        this.chambreRepository = chambreRepository;
    }

    public ChambreDto create(ChambreDto dto) {
        if (dto == null) throw new IllegalArgumentException("Chambre payload manquant");
        if (dto.getNumeroChambre() == null || dto.getNumeroChambre().isBlank()) {
            throw new IllegalArgumentException("numeroChambre manquant");
        }

        Chambre chambre = new Chambre();
        chambre.setNumeroChambre(dto.getNumeroChambre().trim());
        chambre.setActif(dto.isActif());

        Chambre saved = chambreRepository.save(chambre);
        return convertToDto(saved);
    }

    public List<ChambreDto> list() {
        return chambreRepository.findAll().stream().map(this::convertToDto).toList();
    }

    public ChambreDto update(Long id, ChambreDto dto) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (dto == null) throw new IllegalArgumentException("payload manquant");

        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée"));

        if (dto.getNumeroChambre() != null && !dto.getNumeroChambre().isBlank()) {
            chambre.setNumeroChambre(dto.getNumeroChambre().trim());
        }
        chambre.setActif(dto.isActif());

        Chambre saved = chambreRepository.save(chambre);
        return convertToDto(saved);
    }

    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (!chambreRepository.existsById(id)) throw new RuntimeException("Chambre non trouvée");
        chambreRepository.deleteById(id);
    }

    private ChambreDto convertToDto(Chambre chambre) {
        if (chambre == null) return null;

        ChambreDto dto = new ChambreDto();
        dto.setId(chambre.getId());
        dto.setNumeroChambre(chambre.getNumeroChambre());
        dto.setActif(chambre.isActif());
        return dto;
    }
}
