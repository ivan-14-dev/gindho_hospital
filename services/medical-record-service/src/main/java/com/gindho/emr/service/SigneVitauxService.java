package com.gindho.emr.service;

import com.gindho.emr.dto.SigneVitauxDto;
import com.gindho.emr.model.SigneVitaux;
import com.gindho.emr.repository.SigneVitauxRepository;
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
public class SigneVitauxService {
    private final SigneVitauxRepository repository;
    private final EventProducer eventProducer;

    public List<SigneVitauxDto> listByPatient(Long patientId) {
        return repository.findByPatientIdOrderByDateReleveDesc(patientId).stream().map(this::toDto).toList();
    }

    public List<SigneVitauxDto> listByHospitalisation(Long hospitalisationId) {
        return repository.findByHospitalisationIdOrderByDateReleveDesc(hospitalisationId).stream().map(this::toDto).toList();
    }

    @Transactional
    public SigneVitauxDto create(SigneVitauxDto dto) {
        SigneVitaux signe = new SigneVitaux();
        signe.setPatientId(dto.getPatientId());
        signe.setHospitalisationId(dto.getHospitalisationId());
        signe.setDateReleve(dto.getDateReleve() == null ? LocalDateTime.now() : dto.getDateReleve());
        signe.setTemperature(dto.getTemperature());
        signe.setFrequenceCardiaque(dto.getFrequenceCardiaque());
        signe.setFrequenceRespiratoire(dto.getFrequenceRespiratoire());
        signe.setSaturationO2(dto.getSaturationO2());
        signe.setTensionArterielleSys(dto.getTensionArterielleSys());
        signe.setTensionArterielleDia(dto.getTensionArterielleDia());
        signe.setCommentaire(dto.getCommentaire());
        SigneVitaux saved = repository.save(signe);
        eventProducer.publish("medical", BaseEvent.builder()
                .eventType(EventType.PATIENT_UPDATED)
                .source("medical-record-service")
                .payload(toDto(saved))
                .build());
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private SigneVitauxDto toDto(SigneVitaux signe) {
        SigneVitauxDto dto = new SigneVitauxDto();
        dto.setId(signe.getId());
        dto.setPatientId(signe.getPatientId());
        dto.setHospitalisationId(signe.getHospitalisationId());
        dto.setDateReleve(signe.getDateReleve());
        dto.setTemperature(signe.getTemperature());
        dto.setFrequenceCardiaque(signe.getFrequenceCardiaque());
        dto.setFrequenceRespiratoire(signe.getFrequenceRespiratoire());
        dto.setSaturationO2(signe.getSaturationO2());
        dto.setTensionArterielleSys(signe.getTensionArterielleSys());
        dto.setTensionArterielleDia(signe.getTensionArterielleDia());
        dto.setCommentaire(signe.getCommentaire());
        return dto;
    }
}
