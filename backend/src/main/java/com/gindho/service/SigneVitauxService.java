package com.gindho.service;

import com.gindho.dto.SigneVitauxDto;
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
public class SigneVitauxService {
    private final SigneVitauxRepository signeVitauxRepository;
    private final PatientRepository patientRepository;
    private final PatientAccessService patientAccessService;

    public List<SigneVitauxDto> listByPatient(Long patientId) {
        patientAccessService.assertPatientAccess(patientId);
        return signeVitauxRepository.findByPatientIdOrderByDateReleveDesc(patientId)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SigneVitauxDto> listByHospitalisation(Long hospitalisationId) {
        return signeVitauxRepository.findByHospitalisationIdOrderByDateReleveDesc(hospitalisationId)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public SigneVitauxDto create(SigneVitauxDto dto) {
        if (dto.getPatientId() != null) patientAccessService.assertPatientAccess(dto.getPatientId());
        Patient patient = patientRepository.findById(dto.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        SigneVitaux sv = SigneVitaux.builder()
            .temperature(dto.getTemperature()).tensionSystolique(dto.getTensionSystolique())
            .tensionDiastolique(dto.getTensionDiastolique()).frequenceCardiaque(dto.getFrequenceCardiaque())
            .frequenceRespiratoire(dto.getFrequenceRespiratoire()).saturationOxygen(dto.getSaturationOxygen())
            .glycemie(dto.getGlycemie()).poids(dto.getPoids())
            .dateReleve(dto.getDateReleve() != null ? dto.getDateReleve() : java.time.LocalDateTime.now())
            .notes(dto.getNotes()).patient(patient).build();
        return toDto(signeVitauxRepository.save(sv));
    }

    @Transactional
    public void delete(Long id) {
        signeVitauxRepository.deleteById(id);
    }

    private SigneVitauxDto toDto(SigneVitaux sv) {
        String nom = sv.getPatient() != null && sv.getPatient().getUser() != null
            ? sv.getPatient().getUser().getPrenom() + " " + sv.getPatient().getUser().getNom() : "";
        return SigneVitauxDto.builder().id(sv.getId()).temperature(sv.getTemperature())
            .tensionSystolique(sv.getTensionSystolique()).tensionDiastolique(sv.getTensionDiastolique())
            .frequenceCardiaque(sv.getFrequenceCardiaque()).frequenceRespiratoire(sv.getFrequenceRespiratoire())
            .saturationOxygen(sv.getSaturationOxygen()).glycemie(sv.getGlycemie()).poids(sv.getPoids())
            .dateReleve(sv.getDateReleve()).notes(sv.getNotes())
            .patientId(sv.getPatient() != null ? sv.getPatient().getId() : null)
            .hospitalisationId(sv.getHospitalisation() != null ? sv.getHospitalisation().getId() : null)
            .patientNom(nom.trim()).build();
    }
}