package com.gindho.emergency.service;

import com.gindho.emergency.dto.EmergencyTriageDto;
import com.gindho.emergency.model.EmergencyTriage;
import com.gindho.emergency.repository.EmergencyTriageRepository;
import com.gindho.kafka.BaseEvent; import com.gindho.kafka.EventProducer; import com.gindho.kafka.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class EmergencyService {
    private final EmergencyTriageRepository repository;
    private final EventProducer eventProducer;

    @Transactional
    public EmergencyTriageDto trier(EmergencyTriageDto dto) {
        EmergencyTriage t = new EmergencyTriage();
        t.setPatientId(dto.getPatientId()); t.setMedecinId(dto.getMedecinId());
        t.setNiveauTriage(EmergencyTriage.TriageLevel.valueOf(dto.getNiveauTriage()));
        t.setDescription(dto.getDescription()); t.setDateArrivee(LocalDateTime.now());
        repository.save(t);
        eventProducer.publish("emergency", BaseEvent.builder().eventType(EventType.EMERGENCY_TRIAGED).source("emergency-service").build());
        return toDto(t);
    }

    private EmergencyTriageDto toDto(EmergencyTriage t) {
        return EmergencyTriageDto.builder().id(t.getId()).patientId(t.getPatientId())
                .niveauTriage(t.getNiveauTriage().name()).description(t.getDescription())
                .dateArrivee(t.getDateArrivee()).statut(t.getStatut().name()).build();
    }
}