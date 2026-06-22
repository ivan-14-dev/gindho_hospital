package com.gindho.appointment.service;

import com.gindho.appointment.dto.TeleconsultationDto;
import com.gindho.appointment.model.Teleconsultation;
import com.gindho.appointment.repository.TeleconsultationRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TeleconsultationService {
    private final TeleconsultationRepository repository;
    private final EventProducer eventProducer;

    public List<TeleconsultationDto> findByPatient(Long patientId) {
        return repository.findByPatientIdOrderByDatePrevuDesc(patientId).stream().map(this::toDto).toList();
    }

    public List<TeleconsultationDto> findByMedecin(Long medecinId) {
        return repository.findByMedecinIdOrderByDatePrevuDesc(medecinId).stream().map(this::toDto).toList();
    }

    @Transactional
    public TeleconsultationDto create(TeleconsultationDto dto) {
        Teleconsultation teleconsultation = new Teleconsultation();
        teleconsultation.setPatientId(dto.getPatientId());
        teleconsultation.setMedecinId(dto.getMedecinId());
        teleconsultation.setDatePrevu(dto.getDatePrevu());
        teleconsultation.setMotif(dto.getMotif());
        teleconsultation.setStatut(dto.getStatut() == null ? "DEMANDEE" : dto.getStatut());
        teleconsultation.setLienSession(dto.getLienSession());
        Teleconsultation saved = repository.save(teleconsultation);
        publish(saved, EventType.TELECONSULTATION_CREATED);
        return toDto(saved);
    }

    @Transactional
    public TeleconsultationDto updateStatut(Long id, String statut) {
        Teleconsultation teleconsultation = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Téléconsultation introuvable"));
        teleconsultation.setStatut(statut == null || statut.isBlank() ? "DEMANDEE" : statut.trim().toUpperCase());
        Teleconsultation saved = repository.save(teleconsultation);
        publish(saved, EventType.TELECONSULTATION_STATUS_CHANGED);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Téléconsultation introuvable: " + id);
        }
        repository.deleteById(id);
    }

    private void publish(Teleconsultation saved, String eventType) {
        eventProducer.publish("teleconsultation", BaseEvent.builder()
                .eventType(eventType)
                .source("appointment-service")
                .payload(Map.of(
                        "teleconsultationId", saved.getId(),
                        "patientId", saved.getPatientId(),
                        "medecinId", saved.getMedecinId(),
                        "statut", saved.getStatut()
                ))
                .build());
    }

    private TeleconsultationDto toDto(Teleconsultation t) {
        TeleconsultationDto dto = new TeleconsultationDto();
        dto.setId(t.getId());
        dto.setPatientId(t.getPatientId());
        dto.setMedecinId(t.getMedecinId());
        dto.setDatePrevu(t.getDatePrevu());
        dto.setMotif(t.getMotif());
        dto.setStatut(t.getStatut());
        dto.setLienSession(t.getLienSession());
        return dto;
    }
}
