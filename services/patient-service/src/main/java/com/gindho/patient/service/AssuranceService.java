package com.gindho.patient.service;

import com.gindho.patient.dto.AssurancePatientDto;
import com.gindho.patient.model.AssurancePatient;
import com.gindho.patient.repository.AssurancePatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssuranceService {
    private final AssurancePatientRepository repository;

    public List<AssurancePatientDto> findByPatientId(Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssurancePatientDto create(Long patientId, AssurancePatientDto dto) {
        AssurancePatient assurance = new AssurancePatient();
        assurance.setPatientId(patientId);
        assurance.setCompagnie(dto.getCompagnie());
        assurance.setNumeroPolice(dto.getNumeroPolice());
        assurance.setValiditeDebut(dto.getValiditeDebut());
        assurance.setValiditeFin(dto.getValiditeFin());
        assurance.setCouverture(dto.getCouverture());
        assurance.setActif(dto.isActif());
        return toDto(repository.save(assurance));
    }

    @Transactional
    public AssurancePatientDto update(Long assuranceId, AssurancePatientDto dto) {
        AssurancePatient assurance = repository.findById(assuranceId)
                .orElseThrow(() -> new EntityNotFoundException("Assurance introuvable: " + assuranceId));
        if (dto.getPatientId() != null && !dto.getPatientId().equals(assurance.getPatientId())) {
            throw new IllegalArgumentException("L'assurance appartient à un autre patient");
        }
        assurance.setCompagnie(dto.getCompagnie());
        assurance.setNumeroPolice(dto.getNumeroPolice());
        assurance.setValiditeDebut(dto.getValiditeDebut());
        assurance.setValiditeFin(dto.getValiditeFin());
        assurance.setCouverture(dto.getCouverture());
        assurance.setActif(dto.isActif());
        return toDto(repository.save(assurance));
    }

    @Transactional
    public void delete(Long assuranceId) {
        if (!repository.existsById(assuranceId)) {
            throw new EntityNotFoundException("Assurance introuvable: " + assuranceId);
        }
        repository.deleteById(assuranceId);
    }

    private AssurancePatientDto toDto(AssurancePatient assurance) {
        return AssurancePatientDto.builder()
                .id(assurance.getId())
                .patientId(assurance.getPatientId())
                .compagnie(assurance.getCompagnie())
                .numeroPolice(assurance.getNumeroPolice())
                .validiteDebut(assurance.getValiditeDebut())
                .validiteFin(assurance.getValiditeFin())
                .couverture(assurance.getCouverture())
                .actif(assurance.isActif())
                .build();
    }
}
