package com.gindho.insurance.service;

import com.gindho.insurance.dto.AssurancePatientDto;
import com.gindho.insurance.model.AssurancePatient;
import com.gindho.insurance.repository.AssurancePatientRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssurancePatientService {

    private final AssurancePatientRepository repository;
    private final EventProducer eventProducer;

    public List<AssurancePatientDto> findByPatient(Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AssurancePatientDto> findActiveByPatient(Long patientId) {
        return repository.findByPatientIdAndActifTrue(patientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AssurancePatientDto findById(Long id) {
        AssurancePatient ap = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assurance non trouvée: " + id));
        return toDto(ap);
    }

    @Transactional
    public AssurancePatientDto create(AssurancePatientDto dto) {
        if (dto.getPatientId() == null) {
            throw new IllegalArgumentException("patientId requis");
        }

        AssurancePatient ap = AssurancePatient.builder()
                .compagnie(dto.getCompagnie())
                .numeroPolice(dto.getNumeroPolice())
                .typeCouverture(dto.getTypeCouverture())
                .tauxPriseEnCharge(dto.getTauxPriseEnCharge() != null ? dto.getTauxPriseEnCharge() : BigDecimal.valueOf(80.0))
                .plafondAnnuel(dto.getPlafondAnnuel())
                .montantConsomme(dto.getMontantConsomme() != null ? dto.getMontantConsomme() : BigDecimal.ZERO)
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .actif(dto.isActif())
                .patientId(dto.getPatientId())
                .build();

        AssurancePatient saved = repository.save(ap);
        eventProducer.publish("insurance", BaseEvent.builder()
                .eventType(EventType.INSURANCE_CREATED)
                .source("insurance-service")
                .payload(dto)
                .build());
        log.info("Assurance created: id={} patientId={}", saved.getId(), saved.getPatientId());
        return toDto(saved);
    }

    @Transactional
    public AssurancePatientDto update(Long id, AssurancePatientDto dto) {
        AssurancePatient ap = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assurance non trouvée: " + id));

        if (dto.getCompagnie() != null) ap.setCompagnie(dto.getCompagnie());
        if (dto.getNumeroPolice() != null) ap.setNumeroPolice(dto.getNumeroPolice());
        if (dto.getTypeCouverture() != null) ap.setTypeCouverture(dto.getTypeCouverture());
        if (dto.getTauxPriseEnCharge() != null) ap.setTauxPriseEnCharge(dto.getTauxPriseEnCharge());
        if (dto.getPlafondAnnuel() != null) ap.setPlafondAnnuel(dto.getPlafondAnnuel());
        if (dto.getMontantConsomme() != null) ap.setMontantConsomme(dto.getMontantConsomme());
        if (dto.getDateDebut() != null) ap.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) ap.setDateFin(dto.getDateFin());
        ap.setActif(dto.isActif());
        if (dto.getPatientId() != null) ap.setPatientId(dto.getPatientId());

        AssurancePatient saved = repository.save(ap);
        eventProducer.publish("insurance", BaseEvent.builder()
                .eventType(EventType.INSURANCE_UPDATED)
                .source("insurance-service")
                .payload(dto)
                .build());
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Assurance non trouvée: " + id);
        }
        repository.deleteById(id);
    }

    public List<AssurancePatientDto> searchByCompagnie(String compagnie, Pageable pageable) {
        return repository.findByCompagnieContainingIgnoreCase(compagnie, pageable)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private AssurancePatientDto toDto(AssurancePatient ap) {
        return AssurancePatientDto.builder()
                .id(ap.getId())
                .compagnie(ap.getCompagnie())
                .numeroPolice(ap.getNumeroPolice())
                .typeCouverture(ap.getTypeCouverture())
                .tauxPriseEnCharge(ap.getTauxPriseEnCharge())
                .plafondAnnuel(ap.getPlafondAnnuel())
                .montantConsomme(ap.getMontantConsomme())
                .dateDebut(ap.getDateDebut())
                .dateFin(ap.getDateFin())
                .actif(ap.isActif())
                .patientId(ap.getPatientId())
                .build();
    }
}
