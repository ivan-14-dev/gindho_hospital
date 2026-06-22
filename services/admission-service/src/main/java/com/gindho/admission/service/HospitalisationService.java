package com.gindho.admission.service;

import com.gindho.admission.dto.HospitalisationDto;
import com.gindho.admission.model.Hospitalisation;
import com.gindho.admission.repository.HospitalisationRepository;
import com.gindho.kafka.BaseEvent; import com.gindho.kafka.EventProducer; import com.gindho.kafka.EventType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime; import java.util.List; import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class HospitalisationService {
    private final HospitalisationRepository repository;
    private final EventProducer eventProducer;

    public List<Hospitalisation> findByPatient(Long patientId) { return repository.findByPatientIdOrderByDateAdmissionDesc(patientId); }
    public List<Hospitalisation> findAll() { return repository.findAll(); }
    public List<Hospitalisation> findEnCours() { return repository.findByStatut(Hospitalisation.StatutHospitalisation.ADMIS); }
    public Hospitalisation findById(Long id) { return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Hospitalisation not found")); }

    @Transactional
    public HospitalisationDto create(HospitalisationDto dto) {
        Hospitalisation h = new Hospitalisation();
        h.setPatientId(dto.getPatientId()); h.setMedecinId(dto.getMedecinId());
        h.setLitId(dto.getLitId()); h.setDateAdmission(LocalDateTime.now());
        h.setMotif(dto.getMotif()); h.setService(dto.getService());
        h.setStatut(Hospitalisation.StatutHospitalisation.ADMIS);
        repository.save(h);
        eventProducer.publish("medical", BaseEvent.builder().eventType(EventType.ADMISSION_CREATED).source("admission-service").build());
        return toDto(h);
    }

    @Transactional
    public HospitalisationDto rapportSortie(Long id, HospitalisationDto dto) {
        Hospitalisation h = findById(id);
        h.setDateSortie(LocalDateTime.now());
        h.setStatut(Hospitalisation.StatutHospitalisation.SORTI);
        h.setNotes(dto.getNotes());
        repository.save(h);
        eventProducer.publish("medical", BaseEvent.builder().eventType(EventType.DISCHARGE_COMPLETED).source("admission-service").build());
        return toDto(h);
    }

    @Transactional
    public void sortir(Long id) {
        rapportSortie(id, new HospitalisationDto());
    }

    @Transactional
    public HospitalisationDto update(Long id, HospitalisationDto dto) {
        Hospitalisation h = findById(id);
        h.setPatientId(dto.getPatientId());
        h.setMedecinId(dto.getMedecinId());
        h.setLitId(dto.getLitId());
        h.setMotif(dto.getMotif());
        h.setService(dto.getService());
        h.setNotes(dto.getNotes());
        return toDto(repository.save(h));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public HospitalisationDto toDto(Hospitalisation h) {
        return HospitalisationDto.builder().id(h.getId()).patientId(h.getPatientId())
                .medecinId(h.getMedecinId()).litId(h.getLitId())
                .dateAdmission(h.getDateAdmission()).dateSortie(h.getDateSortie())
                .statut(h.getStatut().name()).motif(h.getMotif())
                .service(h.getService()).notes(h.getNotes()).build();
    }
}
