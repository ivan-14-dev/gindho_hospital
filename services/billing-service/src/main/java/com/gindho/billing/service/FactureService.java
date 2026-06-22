package com.gindho.billing.service;

import com.gindho.billing.dto.FactureDto;
import com.gindho.billing.dto.PaiementDto;
import com.gindho.billing.model.Facture;
import com.gindho.billing.model.Paiement;
import com.gindho.billing.model.StatutFacture;
import com.gindho.billing.repository.FactureRepository;
import com.gindho.billing.repository.PaiementRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate; import java.time.LocalDateTime;
import java.util.List; import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class FactureService {
    private final FactureRepository factureRepository;
    private final PaiementRepository paiementRepository;
    private final EventProducer eventProducer;

    public Page<FactureDto> findAll(Pageable p) { return factureRepository.findAll(p).map(this::toDto); }
    public FactureDto findById(Long id) { return toDto(factureRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Facture not found: " + id))); }
    public List<FactureDto> findByPatient(Long patientId) { return factureRepository.findByPatientIdOrderByDateEmissionDesc(patientId).stream().map(this::toDto).collect(Collectors.toList()); }

    @Transactional
    public FactureDto create(FactureDto dto) {
        long cnt = factureRepository.count() + 1;
        String num = String.format("FACT-%d-%04d", LocalDate.now().getYear(), cnt);
        Facture f = new Facture();
        f.setNumeroFacture(num); f.setPatientId(dto.getPatientId());
        f.setMontant(dto.getMontant()); f.setMontantPaye(BigDecimal.ZERO);
        f.setRemise(dto.getRemise() != null ? dto.getRemise() : BigDecimal.ZERO);
        f.setStatut(StatutFacture.EMISE);
        f.setDateEmission(LocalDateTime.now()); f.setDateEcheance(dto.getDateEcheance());
        f.setDescription(dto.getDescription());
        factureRepository.save(f);
        log.info("Invoice created: id={} num={}", f.getId(), f.getNumeroFacture());
        eventProducer.publish("billing", BaseEvent.builder().eventType(EventType.INVOICE_CREATED).source("billing-service").payload(dto).build());
        return toDto(f);
    }

    @Transactional
    public FactureDto payer(Long id, PaiementDto pDto) {
        Facture f = findFacture(id);
        Paiement p = new Paiement();
        p.setFactureId(id); p.setMontant(pDto.getMontant());
        p.setDatePaiement(LocalDateTime.now()); p.setModePaiement(pDto.getModePaiement());
        p.setReference(pDto.getReference());
        paiementRepository.save(p);
        f.setMontantPaye(f.getMontantPaye().add(pDto.getMontant()));
        if (f.getMontantPaye().compareTo(f.getMontant()) >= 0) {
            f.setStatut(StatutFacture.PAYEE);
        } else {
            f.setStatut(StatutFacture.PARTIELLE);
        }
        factureRepository.save(f);
        eventProducer.publish("billing", BaseEvent.builder().eventType(EventType.INVOICE_PAID).source("billing-service").payload(pDto).build());
        return toDto(f);
    }

    public List<FactureDto> findByStatut(StatutFacture statut) {
        return factureRepository.findByStatut(statut).stream().map(this::toDto).collect(Collectors.toList());
    }

    public Page<FactureDto> findByStatut(StatutFacture statut, Pageable pageable) {
        return factureRepository.findByStatut(statut, pageable).map(this::toDto);
    }

    public Page<FactureDto> getByPatient(Long patientId, Pageable pageable) {
        return factureRepository.findByPatientIdOrderByDateEmissionDesc(patientId, pageable).map(this::toDto);
    }

    public List<FactureDto> getByPatient(Long patientId) {
        return factureRepository.findByPatientIdOrderByDateEmissionDesc(patientId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public FactureDto update(Long id, FactureDto dto) {
        Facture f = findFacture(id);
        if (dto.getNumeroFacture() != null) f.setNumeroFacture(dto.getNumeroFacture());
        if (dto.getMontant() != null) f.setMontant(dto.getMontant());
        if (dto.getMontantPaye() != null) f.setMontantPaye(dto.getMontantPaye());
        if (dto.getRemise() != null) f.setRemise(dto.getRemise());
        if (dto.getStatut() != null) f.setStatut(StatutFacture.valueOf(dto.getStatut()));
        if (dto.getDateEcheance() != null) f.setDateEcheance(dto.getDateEcheance());
        if (dto.getDescription() != null) f.setDescription(dto.getDescription());
        if (dto.getNotes() != null) f.setNotes(dto.getNotes());
        factureRepository.save(f);
        return toDto(f);
    }

    @Transactional
    public void annuler(Long id) {
        Facture f = findFacture(id);
        f.setStatut(StatutFacture.ANNULEE);
        factureRepository.save(f);
    }

    private Facture findFacture(Long id) {
        return factureRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Facture not found: " + id));
    }

    private FactureDto toDto(Facture f) {
        return FactureDto.builder().id(f.getId()).numeroFacture(f.getNumeroFacture())
                .patientId(f.getPatientId()).montant(f.getMontant())
                .montantPaye(f.getMontantPaye()).remise(f.getRemise())
                .statut(f.getStatut().name()).dateEmission(f.getDateEmission())
                .dateEcheance(f.getDateEcheance()).description(f.getDescription()).build();
    }
}