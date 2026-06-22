package com.gindho.billing.service;

import com.gindho.billing.dto.RevenuDto;
import com.gindho.billing.model.Revenu;
import com.gindho.billing.repository.RevenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RevenuService {
    private final RevenuRepository repository;

    public Page<RevenuDto> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toDto);
    }

    public List<RevenuDto> findByPatient(Long patientId) {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public List<RevenuDto> findByMedecin(Long medecinId) {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public BigDecimal getTotalRevenus(LocalDateTime start, LocalDateTime end) {
        return repository.findByDateBetweenOrderByDateAsc(start.toLocalDate(), end.toLocalDate()).stream()
                .map(Revenu::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public RevenuDto findById(Long id) {
        return toDto(repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Revenu introuvable")));
    }

    @Transactional
    public RevenuDto create(RevenuDto dto) {
        Revenu revenu = new Revenu();
        revenu.setDate(dto.getDate());
        revenu.setMontantTotal(dto.getMontantTotal());
        revenu.setConsultations(dto.getConsultations());
        revenu.setHospitalisations(dto.getHospitalisations());
        revenu.setAnalyses(dto.getAnalyses());
        revenu.setPharmacie(dto.getPharmacie());
        revenu.setAutres(dto.getAutres());
        return toDto(repository.save(revenu));
    }

    @Transactional
    public RevenuDto update(Long id, RevenuDto dto) {
        Revenu revenu = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Revenu introuvable"));
        revenu.setDate(dto.getDate());
        revenu.setMontantTotal(dto.getMontantTotal());
        revenu.setConsultations(dto.getConsultations());
        revenu.setHospitalisations(dto.getHospitalisations());
        revenu.setAnalyses(dto.getAnalyses());
        revenu.setPharmacie(dto.getPharmacie());
        revenu.setAutres(dto.getAutres());
        return toDto(repository.save(revenu));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private RevenuDto toDto(Revenu r) {
        return RevenuDto.builder()
                .id(r.getId())
                .date(r.getDate())
                .montantTotal(r.getMontantTotal())
                .consultations(r.getConsultations())
                .hospitalisations(r.getHospitalisations())
                .analyses(r.getAnalyses())
                .pharmacie(r.getPharmacie())
                .autres(r.getAutres())
                .build();
    }
}
