package com.gindho.billing.service;

import com.gindho.billing.dto.PaiementDto;
import com.gindho.billing.model.Paiement;
import com.gindho.billing.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class PaiementService {
    private final PaiementRepository repository;
    public List<PaiementDto> findByFacture(Long factureId) {
        return repository.findByFactureId(factureId).stream().map(this::toDto).collect(Collectors.toList());
    }
    private PaiementDto toDto(Paiement p) {
        return PaiementDto.builder().id(p.getId()).factureId(p.getFactureId())
                .montant(p.getMontant()).datePaiement(p.getDatePaiement())
                .modePaiement(p.getModePaiement()).reference(p.getReference()).build();
    }
}