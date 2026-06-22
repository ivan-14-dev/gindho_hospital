package com.gindho.emr.service;

import com.gindho.emr.dto.ConsultationDto;
import com.gindho.emr.dto.DiagnosticDto;
import com.gindho.emr.dto.ObservationDto;
import com.gindho.emr.model.Consultation;
import com.gindho.emr.model.Diagnostic;
import com.gindho.emr.model.Observation;
import com.gindho.emr.repository.ConsultationRepository;
import com.gindho.emr.repository.DiagnosticRepository;
import com.gindho.emr.repository.ObservationRepository;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final DiagnosticRepository diagnosticRepository;
    private final ObservationRepository observationRepository;
    private final EventProducer eventProducer;

    public List<ConsultationDto> findByPatient(Long patientId) {
        return consultationRepository.findByPatientIdOrderByDateConsultationDesc(patientId).stream().map(this::toDto).toList();
    }

    public List<ConsultationDto> findByMedecin(Long medecinId) {
        return consultationRepository.findByMedecinIdOrderByDateConsultationDesc(medecinId).stream().map(this::toDto).toList();
    }

    @Transactional
    public ConsultationDto create(ConsultationDto dto) {
        Consultation consultation = Consultation.builder()
                .patientId(dto.getPatientId())
                .medecinId(dto.getMedecinId())
                .dateConsultation(dto.getDateConsultation())
                .motif(dto.getMotif())
                .conclusion(dto.getConclusion())
                .type(dto.getType())
                .build();
        Consultation saved = consultationRepository.save(consultation);
        eventProducer.publish("medical", BaseEvent.builder()
                .eventType("ObservationCreated")
                .source("medical-record-service")
                .payload(toDto(saved))
                .build());
        return toDto(saved);
    }

    @Transactional
    public DiagnosticDto createDiagnostic(DiagnosticDto dto) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setConsultationId(dto.getConsultationId());
        diagnostic.setCodeCim10(dto.getCodeCim10());
        diagnostic.setLibelle(dto.getLibelle());
        diagnostic.setType(dto.getType());
        return toDto(diagnosticRepository.save(diagnostic));
    }

    @Transactional
    public ObservationDto createObservation(ObservationDto dto) {
        Observation observation = new Observation();
        observation.setConsultationId(dto.getConsultationId());
        observation.setType(dto.getType());
        observation.setContenu(dto.getContenu());
        Observation saved = observationRepository.save(observation);
        eventProducer.publish("medical", BaseEvent.builder()
                .eventType("ObservationCreated")
                .source("medical-record-service")
                .payload(toDto(saved))
                .build());
        return toDto(saved);
    }

    public List<ObservationDto> observations(Long consultationId) {
        return observationRepository.findByConsultationId(consultationId).stream().map(this::toDto).toList();
    }

    public List<DiagnosticDto> diagnostics(Long consultationId) {
        return diagnosticRepository.findByConsultationId(consultationId).stream().map(this::toDto).toList();
    }

    private ConsultationDto toDto(Consultation c) {
        return ConsultationDto.builder()
                .id(c.getId())
                .patientId(c.getPatientId())
                .medecinId(c.getMedecinId())
                .dateConsultation(c.getDateConsultation())
                .motif(c.getMotif())
                .conclusion(c.getConclusion())
                .type(c.getType())
                .build();
    }

    private DiagnosticDto toDto(Diagnostic d) {
        DiagnosticDto dto = new DiagnosticDto();
        dto.setId(d.getId());
        dto.setConsultationId(d.getConsultationId());
        dto.setCodeCim10(d.getCodeCim10());
        dto.setLibelle(d.getLibelle());
        dto.setType(d.getType());
        return dto;
    }

    private ObservationDto toDto(Observation o) {
        ObservationDto dto = new ObservationDto();
        dto.setId(o.getId());
        dto.setConsultationId(o.getConsultationId());
        dto.setType(o.getType());
        dto.setContenu(o.getContenu());
        return dto;
    }
}
