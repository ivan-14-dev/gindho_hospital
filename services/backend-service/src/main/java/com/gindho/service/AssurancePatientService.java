package com.gindho.service;

import com.gindho.dto.AssurancePatientDto;
import com.gindho.model.AssurancePatient;
import com.gindho.model.Patient;
import com.gindho.repository.AssurancePatientRepository;
import com.gindho.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssurancePatientService {

    private final AssurancePatientRepository assuranceRepository;
    private final PatientRepository patientRepository;
    private final PatientAccessService patientAccessService;

    public List<AssurancePatientDto> listByPatient(Long patientId) {
        patientAccessService.assertPatientAccess(patientId);
        return assuranceRepository.findByPatientId(patientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AssurancePatientDto> listActivesByPatient(Long patientId) {
        patientAccessService.assertPatientAccess(patientId);
        return assuranceRepository.findByPatientIdAndActifTrue(patientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AssurancePatientDto getById(Long id) {
        AssurancePatient ap = assuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assurance non trouvée"));
        if (ap.getPatient() != null && ap.getPatient().getId() != null) {
            patientAccessService.assertPatientAccess(ap.getPatient().getId());
        }
        return toDto(ap);
    }

    @Transactional
    public AssurancePatientDto create(AssurancePatientDto dto) {
        if (dto.getPatientId() == null) {
            throw new IllegalArgumentException("patientId requis");
        }
        patientAccessService.assertPatientAccess(dto.getPatientId());

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        AssurancePatient ap = AssurancePatient.builder()
                .compagnie(dto.getCompagnie())
                .numeroPolice(dto.getNumeroPolice())
                .typeCouverture(dto.getTypeCouverture())
                .tauxPriseEnCharge(dto.getTauxPriseEnCharge() != null ? dto.getTauxPriseEnCharge() : BigDecimal.valueOf(80.0))
                .plafondAnnuel(dto.getPlafondAnnuel())
                .montantConsomme(dto.getMontantConsomme() != null ? dto.getMontantConsomme() : BigDecimal.ZERO)
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .actif(dto.isActif())
                .patient(patient)
                .build();

        AssurancePatient saved = assuranceRepository.save(ap);
        return toDto(saved);
    }

    @Transactional
    public AssurancePatientDto update(Long id, AssurancePatientDto dto) {
        AssurancePatient ap = assuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assurance non trouvée"));

        if (ap.getPatient() != null && ap.getPatient().getId() != null) {
            patientAccessService.assertPatientAccess(ap.getPatient().getId());
        }

        if (dto.getCompagnie() != null) ap.setCompagnie(dto.getCompagnie());
        if (dto.getNumeroPolice() != null) ap.setNumeroPolice(dto.getNumeroPolice());
        if (dto.getTypeCouverture() != null) ap.setTypeCouverture(dto.getTypeCouverture());
        if (dto.getTauxPriseEnCharge() != null) ap.setTauxPriseEnCharge(dto.getTauxPriseEnCharge());
        if (dto.getPlafondAnnuel() != null) ap.setPlafondAnnuel(dto.getPlafondAnnuel());
        if (dto.getMontantConsomme() != null) ap.setMontantConsomme(dto.getMontantConsomme());
        if (dto.getDateDebut() != null) ap.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) ap.setDateFin(dto.getDateFin());
        ap.setActif(dto.isActif());

        if (dto.getPatientId() != null) {
            patientAccessService.assertPatientAccess(dto.getPatientId());
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
            ap.setPatient(patient);
        }

        AssurancePatient saved = assuranceRepository.save(ap);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        AssurancePatient ap = assuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assurance non trouvée"));
        if (ap.getPatient() != null && ap.getPatient().getId() != null) {
            patientAccessService.assertPatientAccess(ap.getPatient().getId());
        }
        assuranceRepository.deleteById(id);
    }

    public List<AssurancePatientDto> searchByCompagnie(String compagnie) {
        return assuranceRepository.findByCompagnieContainingIgnoreCase(compagnie)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AssurancePatientDto toDto(AssurancePatient ap) {
        String patientNom = "";
        if (ap.getPatient() != null && ap.getPatient().getUser() != null) {
            patientNom = (ap.getPatient().getUser().getPrenom() != null ? ap.getPatient().getUser().getPrenom() : "")
                    + " " + (ap.getPatient().getUser().getNom() != null ? ap.getPatient().getUser().getNom() : "");
            patientNom = patientNom.trim();
        }

        return AssurancePatientDto.builder()
                .id(ap.getId())
                .compagnie(ap.getCompagnie())
                .numeroPolice(ap.getNumeroPolice())
                .typeCouverture(ap.getTypeCouverture())
                .tauxPriseEnCharge(ap.getTauxPriseEnCharge())
                .plafondAnnuel(ap.getPlafondAnnuel())
                .montantConsomme(ap.getMontantConsomme())
                .dateDebut(ap.getDateDebut())
                .dateFin(ap.getDateFin())
                .actif(ap.isActif())
                .patientId(ap.getPatient() != null ? ap.getPatient().getId() : null)
                .patientNom(patientNom)
                .build();
    }
}
