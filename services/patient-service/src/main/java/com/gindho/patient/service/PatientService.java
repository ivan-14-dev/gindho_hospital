package com.gindho.patient.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import com.gindho.patient.dto.PatientDashboardDto;
import com.gindho.patient.dto.PatientDossierResponse;
import com.gindho.patient.dto.PatientDto;
import com.gindho.patient.model.Patient;
import com.gindho.patient.repository.PatientRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final EventProducer eventProducer;
    private final ObjectMapper objectMapper;

    // ========== Backward-compatible methods ==========

    public Page<Patient> findAll(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found: " + id));
    }

    public Optional<Patient> findByUserId(Long userId) {
        return patientRepository.findByUserIdAndActifTrue(userId);
    }

    public Optional<Patient> findCurrentPatientByUserId(Long userId) {
        return patientRepository.findByUserIdAndActifTrue(userId);
    }

    public PatientDossierResponse getDossier(Long id) {
        Patient patient = findById(id);
        return PatientDossierResponse.builder()
                .patient(toDto(patient))
                .resume(patient.getNom() + " " + patient.getPrenom())
                .build();
    }

    public PatientDashboardDto getDashboard(Long id) {
        Patient patient = findById(id);
        return PatientDashboardDto.builder()
                .patientId(patient.getId())
                .nomComplet(patient.getNom() + " " + patient.getPrenom())
                .build();
    }

    public Optional<Patient> findByNumeroPatient(String num) {
        return patientRepository.findByNumeroPatientAndActifTrue(num);
    }

    public List<Patient> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return patientRepository.findAll(pageable).getContent();
        }
        return patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(query, query, pageable);
    }

    @Transactional
    public PatientDto create(PatientDto dto) {
        if (dto.getUserId() != null) {
            Optional<Patient> existing = patientRepository.findByUserIdAndActifTrue(dto.getUserId());
            if (existing.isPresent()) {
                log.warn("Patient already exists for userId={}, returning existing patient id={}", dto.getUserId(), existing.get().getId());
                return toDto(existing.get());
            }
        }

        long count = patientRepository.count() + 1;
        String num = String.format("PAT-%d-%04d", LocalDate.now().getYear(), count);
        Patient patient = new Patient();
        patient.setNumeroPatient(num);
        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setEmail(dto.getEmail());
        if (dto.getSexe() != null) patient.setSexe(Patient.Sexe.valueOf(dto.getSexe()));
        patient.setTelephone(dto.getTelephone());
        patient.setGroupeSanguin(dto.getGroupeSanguin());
        patient.setTaille(dto.getTaille());
        patient.setVille(dto.getVille());
        patient.setAntecedents(dto.getAntecedents());
        patient.setAllergies(dto.getAllergies());
        patient.setAdresse(dto.getAdresse());
        patient.setUserId(dto.getUserId());
        patient.setActif(true);
        patient = patientRepository.save(patient);
        log.info("Patient created: id={} numero={}", patient.getId(), patient.getNumeroPatient());

        eventProducer.publish("patient", BaseEvent.builder()
                .eventType(EventType.PATIENT_CREATED)
                .source("patient-service")
                .payload(dto)
                .build());

        return toDto(patient);
    }

    @Transactional
    public PatientDto update(Long id, PatientDto dto) {
        Patient p = findById(id);
        if (dto.getDateNaissance() != null) p.setDateNaissance(dto.getDateNaissance());
        if (dto.getTelephone() != null) p.setTelephone(dto.getTelephone());
        if (dto.getAdresse() != null) p.setAdresse(dto.getAdresse());
        if (dto.getNom() != null) p.setNom(dto.getNom());
        if (dto.getPrenom() != null) p.setPrenom(dto.getPrenom());
        if (dto.getEmail() != null) p.setEmail(dto.getEmail());
        if (dto.getAllergies() != null) p.setAllergies(dto.getAllergies());
        if (dto.getAntecedents() != null) p.setAntecedents(dto.getAntecedents());
        if (dto.getGroupeSanguin() != null) p.setGroupeSanguin(dto.getGroupeSanguin());
        if (dto.getUserId() != null) p.setUserId(dto.getUserId());
        patientRepository.save(p);

        eventProducer.publish("patient", BaseEvent.builder()
                .eventType(EventType.PATIENT_UPDATED)
                .source("patient-service")
                .payload(dto)
                .build());

        return toDto(p);
    }

    @Transactional
    public void delete(Long id) {
        Patient p = findById(id);
        p.setActif(false);
        p.setDateArchivage(LocalDateTime.now());
        patientRepository.save(p);
        log.info("Patient soft-deleted: id={}", id);
    }

    // ========== New DTO-based methods for controller ==========

    public Page<PatientDto> listPatients(String search, Pageable pageable) {
        Page<Patient> page;
        if (search != null && !search.isBlank()) {
            List<Patient> patients = patientRepository
                    .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(search, search, pageable);
            long total = patientRepository.count();
            page = new PageImpl<>(patients, pageable, total);
        } else {
            page = patientRepository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    public Optional<PatientDto> getPatient(Long id) {
        return patientRepository.findById(id)
                .filter(Patient::isActif)
                .map(this::toDto);
    }

    @Transactional
    public PatientDto createPatient(PatientDto dto) {
        return create(dto);
    }

    @Transactional
    public PatientDto updatePatient(Long id, PatientDto dto) {
        return update(id, dto);
    }

    @Transactional
    public void deletePatient(Long id) {
        delete(id);
    }

    public Optional<PatientDossierResponse> getDossierComplet(Long id) {
        return patientRepository.findById(id)
                .filter(Patient::isActif)
                .map(patient -> {
                    PatientDossierResponse dossier = getDossier(id);
                    dossier.setPatient(toDto(patient));
                    dossier.setResume(patient.getNom() + " " + patient.getPrenom()
                            + " - " + (patient.getDateNaissance() != null ? patient.getDateNaissance().toString() : "N/A"));
                    return dossier;
                });
    }

    public Optional<PatientDto> getPatientByUserId(Long userId) {
        return patientRepository.findByUserIdAndActifTrue(userId)
                .map(this::toDto);
    }

    public Optional<PatientDashboardDto> getPatientDashboard(Long id) {
        return patientRepository.findById(id)
                .filter(Patient::isActif)
                .map(patient -> PatientDashboardDto.builder()
                        .patientId(patient.getId())
                        .nomComplet(patient.getNom() + " " + patient.getPrenom())
                        .consultations(0)
                        .rendezVous(0)
                        .hospitalisations(0)
                        .documents(0)
                        .build());
    }

    public PatientDashboardDto getGlobalStats() {
        long totalActifs = patientRepository.countByActifTrue();
        long totalInactifs = patientRepository.countByActifFalse();
        return PatientDashboardDto.builder()
                .patientId(0L)
                .nomComplet("Statistiques globales")
                .consultations((int) totalActifs)
                .rendezVous((int) patientRepository.countActivePatientsWithContacts())
                .hospitalisations((int) patientRepository.countActivePatientsWithAssurances())
                .documents((int) patientRepository.countActivePatientsWithDocuments())
                .build();
    }

    public PatientDto toDto(Patient p) {
        return PatientDto.builder()
                .id(p.getId())
                .numeroPatient(p.getNumeroPatient())
                .nom(p.getNom())
                .prenom(p.getPrenom())
                .userId(p.getUserId())
                .email(p.getEmail())
                .dateNaissance(p.getDateNaissance())
                .sexe(p.getSexe() != null ? p.getSexe().name() : null)
                .telephone(p.getTelephone())
                .groupeSanguin(p.getGroupeSanguin())
                .taille(p.getTaille())
                .ville(p.getVille())
                .antecedents(p.getAntecedents())
                .allergies(p.getAllergies())
                .adresse(p.getAdresse())
                .actif(p.isActif())
                .build();
    }
}