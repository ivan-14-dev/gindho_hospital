package com.gindho.service;

import com.gindho.dto.AdministrationMedicamentDto;
import com.gindho.model.*;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdministrationMedicamentService {
    private final AdministrationMedicamentRepository adminMedRepo;
    private final PatientRepository patientRepository;
    private final PatientAccessService patientAccessService;

    public List<AdministrationMedicamentDto> listByPatient(Long patientId) {
        patientAccessService.assertPatientAccess(patientId);
        return adminMedRepo.findByPatientIdOrderByDateAdministrationDesc(patientId)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public AdministrationMedicamentDto create(AdministrationMedicamentDto dto) {
        if (dto.getPatientId() != null) patientAccessService.assertPatientAccess(dto.getPatientId());
        Patient patient = patientRepository.findById(dto.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        AdministrationMedicament am = AdministrationMedicament.builder()
            .medicament(dto.getMedicament()).posologie(dto.getPosologie())
            .voieAdministration(dto.getVoieAdministration())
            .dateAdministration(dto.getDateAdministration() != null ? dto.getDateAdministration() : java.time.LocalDateTime.now())
            .administre(false).notes(dto.getNotes()).patient(patient).build();
        return toDto(adminMedRepo.save(am));
    }

    @Transactional
    public AdministrationMedicamentDto marquerAdministre(Long id) {
        AdministrationMedicament am = adminMedRepo.findById(id).orElseThrow(() -> new RuntimeException("Non trouvé"));
        am.setAdministre(true);
        return toDto(adminMedRepo.save(am));
    }

    private AdministrationMedicamentDto toDto(AdministrationMedicament am) {
        String nom = am.getPatient() != null && am.getPatient().getUser() != null
            ? am.getPatient().getUser().getPrenom() + " " + am.getPatient().getUser().getNom() : "";
        return AdministrationMedicamentDto.builder().id(am.getId()).medicament(am.getMedicament())
            .posologie(am.getPosologie()).voieAdministration(am.getVoieAdministration())
            .dateAdministration(am.getDateAdministration()).administre(am.isAdministre())
            .notes(am.getNotes()).patientId(am.getPatient() != null ? am.getPatient().getId() : null)
            .hospitalisationId(am.getHospitalisation() != null ? am.getHospitalisation().getId() : null)
            .patientNom(nom.trim()).build();
    }
}