package com.gindho.emr.service;

import com.gindho.emr.dto.PlanSoinDto;
import com.gindho.emr.model.PlanSoin;
import com.gindho.emr.repository.PlanSoinRepository;
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
public class PlanSoinService {
    private final PlanSoinRepository repository;
    private final EventProducer eventProducer;

    public List<PlanSoinDto> listByPatient(Long patientId) {
        return repository.findByPatientIdOrderByDatePrevuAsc(patientId).stream().map(this::toDto).toList();
    }

    public List<PlanSoinDto> listByHospitalisation(Long hospitalisationId) {
        return repository.findByHospitalisationIdOrderByDatePrevuAsc(hospitalisationId).stream().map(this::toDto).toList();
    }

    @Transactional
    public PlanSoinDto create(PlanSoinDto dto) {
        PlanSoin plan = new PlanSoin();
        plan.setPatientId(dto.getPatientId());
        plan.setHospitalisationId(dto.getHospitalisationId());
        plan.setIntitule(dto.getIntitule());
        plan.setDescription(dto.getDescription());
        plan.setDatePrevu(dto.getDatePrevu());
        PlanSoin saved = repository.save(plan);
        return toDto(saved);
    }

    @Transactional
    public PlanSoinDto marquerRealise(Long id, String notes) {
        PlanSoin plan = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Plan de soin introuvable"));
        plan.setRealise(true);
        plan.setDateRealisation(LocalDateTime.now());
        plan.setNotesRealisation(notes);
        PlanSoin saved = repository.save(plan);
        eventProducer.publish("medical", BaseEvent.builder()
                .eventType("CarePlanRealized")
                .source("medical-record-service")
                .payload(toDto(saved))
                .build());
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private PlanSoinDto toDto(PlanSoin plan) {
        PlanSoinDto dto = new PlanSoinDto();
        dto.setId(plan.getId());
        dto.setPatientId(plan.getPatientId());
        dto.setHospitalisationId(plan.getHospitalisationId());
        dto.setIntitule(plan.getIntitule());
        dto.setDescription(plan.getDescription());
        dto.setDatePrevu(plan.getDatePrevu());
        dto.setRealise(plan.isRealise());
        dto.setDateRealisation(plan.getDateRealisation());
        dto.setNotesRealisation(plan.getNotesRealisation());
        return dto;
    }
}
