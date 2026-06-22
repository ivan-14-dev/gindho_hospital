package com.gindho.emr.service;

import com.gindho.emr.dto.AdministrationMedicamentDto;
import com.gindho.emr.model.AdministrationMedicament;
import com.gindho.emr.repository.AdministrationMedicamentRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministrationMedicamentService {
    private final AdministrationMedicamentRepository repository;
    private final EventProducer eventProducer;

    public List<AdministrationMedicamentDto> listByPatient(Long patientId) {
        return repository.findByPatientIdOrderByDatePrevuAsc(patientId).stream().map(this::toDto).toList();
    }

    @Transactional
    public AdministrationMedicamentDto create(AdministrationMedicamentDto dto) {
        AdministrationMedicament administration = new AdministrationMedicament();
        administration.setPatientId(dto.getPatientId());
        administration.setHospitalisationId(dto.getHospitalisationId());
        administration.setMedicament(dto.getMedicament());
        administration.setDosage(dto.getDosage());
        administration.setVoie(dto.getVoie());
        administration.setDatePrevu(dto.getDatePrevu());
        AdministrationMedicament saved = repository.save(administration);
        return toDto(saved);
    }

    @Transactional
    public AdministrationMedicamentDto marquerAdministre(Long id) {
        AdministrationMedicament administration = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Administration introuvable"));
        administration.setAdministre(true);
        administration.setDateAdministration(LocalDateTime.now());
        AdministrationMedicament saved = repository.save(administration);
        eventProducer.publish("medical", BaseEvent.builder()
                .eventType(EventType.MEDICATION_ADMINISTERED)
                .source("medical-record-service")
                .payload(toDto(saved))
                .build());
        return toDto(saved);
    }

    private AdministrationMedicamentDto toDto(AdministrationMedicament administration) {
        AdministrationMedicamentDto dto = new AdministrationMedicamentDto();
        dto.setId(administration.getId());
        dto.setPatientId(administration.getPatientId());
        dto.setHospitalisationId(administration.getHospitalisationId());
        dto.setMedicament(administration.getMedicament());
        dto.setDosage(administration.getDosage());
        dto.setVoie(administration.getVoie());
        dto.setDatePrevu(administration.getDatePrevu());
        dto.setAdministre(administration.isAdministre());
        dto.setDateAdministration(administration.getDateAdministration());
        dto.setObservation(administration.getObservation());
        return dto;
    }
}
