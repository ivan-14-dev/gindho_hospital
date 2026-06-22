package com.gindho.service;

import com.gindho.dto.StockConsommableDto;
import com.gindho.model.StockConsommable;
import com.gindho.repository.StockConsommableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockConsommableService {
    private final StockConsommableRepository stockConsommableRepository;

    public List<StockConsommableDto> list() {
        return stockConsommableRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<StockConsommableDto> alerterRupture() {
        return stockConsommableRepository.findByQuantiteLessThan(10)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<StockConsommableDto> alerterPeremption() {
        return stockConsommableRepository.findByDatePeremptionBefore(java.time.LocalDate.now().plusMonths(1))
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public StockConsommableDto create(StockConsommableDto dto) {
        StockConsommable sc = StockConsommable.builder().nom(dto.getNom()).categorie(dto.getCategorie())
            .quantite(dto.getQuantite()).seuilAlerte(dto.getSeuilAlerte() > 0 ? dto.getSeuilAlerte() : 10)
            .prixUnitaire(dto.getPrixUnitaire()).datePeremption(dto.getDatePeremption()).actif(true).build();
        return toDto(stockConsommableRepository.save(sc));
    }

    private StockConsommableDto toDto(StockConsommable sc) {
        return StockConsommableDto.builder().id(sc.getId()).nom(sc.getNom()).categorie(sc.getCategorie())
            .quantite(sc.getQuantite()).seuilAlerte(sc.getSeuilAlerte()).prixUnitaire(sc.getPrixUnitaire())
            .datePeremption(sc.getDatePeremption()).actif(sc.isActif()).build();
    }
}