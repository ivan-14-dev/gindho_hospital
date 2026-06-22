package com.gindho.laboratory.service;

import com.gindho.laboratory.dto.AnalyseDto;
import com.gindho.laboratory.model.Analyse;
import com.gindho.laboratory.repository.AnalyseRepository;
import com.gindho.kafka.BaseEvent; import com.gindho.kafka.EventProducer; import com.gindho.kafka.EventType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime; import java.util.List; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class AnalyseService {
    private final AnalyseRepository repository;
    private final EventProducer eventProducer;

    public List<Analyse> findByPatient(Long patientId) { return repository.findByPatientIdOrderByDatePrescriptionDesc(patientId); }
    public List<Analyse> findByMedecin(Long medecinId) { return repository.findByMedecinIdOrderByDatePrescriptionDesc(medecinId); }
    public List<Analyse> findUrgent() { return repository.findByUrgentTrueOrderByDatePrescriptionDesc(); }
    public Analyse findById(Long id) { return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Analyse not found: " + id)); }

    @Transactional
    public AnalyseDto create(AnalyseDto dto) {
        Analyse a = new Analyse();
        a.setPatientId(dto.getPatientId()); a.setMedecinId(dto.getMedecinId());
        a.setTypeAnalyse(dto.getTypeAnalyse()); a.setDescription(dto.getDescription());
        a.setDatePrescription(LocalDateTime.now()); a.setUrgent(dto.isUrgent());
        repository.save(a);
        return toDto(a);
    }

    @Transactional
    public AnalyseDto ajouterResultat(Long id, String resultat) {
        Analyse a = findById(id);
        a.setResultat(resultat); a.setDateResultat(LocalDateTime.now());
        a.setStatut(Analyse.StatutAnalyse.TERMINE);
        repository.save(a);
        eventProducer.publish("medical", BaseEvent.builder().eventType(EventType.LAB_RESULT_READY).source("laboratory-service").build());
        return toDto(a);
    }

    @Transactional
    public AnalyseDto update(Long id, AnalyseDto dto) {
        Analyse a = findById(id);
        a.setPatientId(dto.getPatientId());
        a.setMedecinId(dto.getMedecinId());
        a.setTypeAnalyse(dto.getTypeAnalyse());
        a.setCode(dto.getCode());
        a.setDescription(dto.getDescription());
        a.setUrgent(dto.isUrgent());
        if (dto.getStatut() != null) a.setStatut(Analyse.StatutAnalyse.valueOf(dto.getStatut()));
        return toDto(repository.save(a));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private AnalyseDto toDto(Analyse a) {
        return AnalyseDto.builder().id(a.getId()).patientId(a.getPatientId())
                .medecinId(a.getMedecinId()).typeAnalyse(a.getTypeAnalyse())
                .description(a.getDescription()).datePrescription(a.getDatePrescription())
                .dateResultat(a.getDateResultat()).resultat(a.getResultat())
                .statut(a.getStatut().name()).urgent(a.isUrgent()).build();
    }
}