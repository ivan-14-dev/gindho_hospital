package com.gindho.service;

import com.gindho.dto.LitDto;
import com.gindho.model.Chambre;
import com.gindho.model.Lit;
import com.gindho.repository.ChambreRepository;
import com.gindho.repository.LitRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LitService {

    private final LitRepository litRepository;
    private final ChambreRepository chambreRepository;

    public LitService(LitRepository litRepository, ChambreRepository chambreRepository) {
        this.litRepository = litRepository;
        this.chambreRepository = chambreRepository;
    }

    public LitDto create(LitDto dto) {
        if (dto == null) throw new IllegalArgumentException("Lit payload manquant");
        if (dto.getNumeroLit() == null || dto.getNumeroLit().isBlank()) {
            throw new IllegalArgumentException("numeroLit manquant");
        }
        if (dto.getChambreId() == null) {
            throw new IllegalArgumentException("chambreId manquant");
        }

        Chambre chambre = chambreRepository.findById(dto.getChambreId())
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée"));

        Lit lit = new Lit();
        lit.setNumeroLit(dto.getNumeroLit().trim());
        lit.setActif(dto.isActif());
        lit.setStatut(dto.getStatut());
        lit.setChambre(chambre);

        Lit saved = litRepository.save(lit);
        return convertToDto(saved);
    }

    public List<LitDto> list() {
        return litRepository.findAll().stream().map(this::convertToDto).toList();
    }

    public LitDto update(Long id, LitDto dto) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (dto == null) throw new IllegalArgumentException("payload manquant");

        Lit lit = litRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lit non trouvé"));

        if (dto.getNumeroLit() != null && !dto.getNumeroLit().isBlank()) {
            lit.setNumeroLit(dto.getNumeroLit().trim());
        }
        lit.setActif(dto.isActif());
        if (dto.getStatut() != null) {
            lit.setStatut(dto.getStatut());
        }

        if (dto.getChambreId() != null) {
            Chambre chambre = chambreRepository.findById(dto.getChambreId())
                    .orElseThrow(() -> new RuntimeException("Chambre non trouvée"));
            lit.setChambre(chambre);
        }

        Lit saved = litRepository.save(lit);
        return convertToDto(saved);
    }

    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id manquant");
        if (!litRepository.existsById(id)) throw new RuntimeException("Lit non trouvé");
        litRepository.deleteById(id);
    }

    public List<LitDto> listByChambre(Long chambreId) {
        if (chambreId == null) throw new IllegalArgumentException("chambreId manquant");
        return litRepository.findByChambreIdAndActifTrue(chambreId).stream()
                .map(this::convertToDto)
                .toList();
    }

    private LitDto convertToDto(Lit lit) {
        if (lit == null) return null;

        LitDto dto = new LitDto();
        dto.setId(lit.getId());
        dto.setNumeroLit(lit.getNumeroLit());
        dto.setActif(lit.isActif());
        dto.setStatut(lit.getStatut());

        if (lit.getChambre() != null) {
            dto.setChambreId(lit.getChambre().getId());
            dto.setChambreNumero(lit.getChambre().getNumeroChambre());
        }

        return dto;
    }
}
