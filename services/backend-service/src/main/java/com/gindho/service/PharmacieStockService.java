package com.gindho.service;

import com.gindho.dto.PharmacieStockDto;
import com.gindho.model.PharmacieStock;
import com.gindho.repository.PharmacieStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PharmacieStockService {
    private final PharmacieStockRepository pharmacieStockRepository;

    public List<PharmacieStockDto> list() {
        return pharmacieStockRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public PharmacieStockDto create(PharmacieStockDto dto) {
        PharmacieStock ps = PharmacieStock.builder().medicament(dto.getMedicament()).lot(dto.getLot())
            .quantite(dto.getQuantite()).prixUnitaire(dto.getPrixUnitaire())
            .datePeremption(dto.getDatePeremption()).dateEntree(dto.getDateEntree() != null ? dto.getDateEntree() : java.time.LocalDate.now())
            .actif(true).build();
        return toDto(pharmacieStockRepository.save(ps));
    }

    @Transactional
    public PharmacieStockDto updateQuantite(Long id, int quantite) {
        PharmacieStock ps = pharmacieStockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock non trouvé"));
        ps.setQuantite(quantite);
        return toDto(pharmacieStockRepository.save(ps));
    }

    public List<PharmacieStockDto> search(String medicament) {
        return pharmacieStockRepository.findByMedicamentContainingIgnoreCase(medicament)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    private PharmacieStockDto toDto(PharmacieStock ps) {
        return PharmacieStockDto.builder().id(ps.getId()).medicament(ps.getMedicament()).lot(ps.getLot())
            .quantite(ps.getQuantite()).prixUnitaire(ps.getPrixUnitaire())
            .datePeremption(ps.getDatePeremption()).dateEntree(ps.getDateEntree()).actif(ps.isActif()).build();
    }
}