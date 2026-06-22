package com.gindho.service;

import com.gindho.dto.PlanSoinDto;
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
public class PlanSoinService {
    private final PlanSoinRepository planSoinRepository;
    private final PatientRepository patientRepository;
    private final PatientAccessService patientAccessService;

    public List<PlanSoinDto> listByPatient(Long patientId) {
        patientAccessService.assertPatientAccess(patientId);
        return planSoinRepository.findByPatientIdOrderByDateSoinDesc(patientId)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<PlanSoinDto> listByHospitalisation(Long hospitalisationId) {
        return planSoinRepository.findByHospitalisationIdOrderByDateSoinAsc(hospitalisationId)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public PlanSoinDto create(PlanSoinDto dto) {
        if (dto.getPatientId() != null) patientAccessService.assertPatientAccess(dto.getPatientId());
        Patient patient = patientRepository.findById(dto.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        PlanSoin ps = PlanSoin.builder().typeSoin(dto.getTypeSoin()).description(dto.getDescription())
            .dateSoin(dto.getDateSoin()).realise(false).notesInfirmier(dto.getNotesInfirmier())
            .patient(patient).build();
        return toDto(planSoinRepository.save(ps));
    }

    @Transactional
    public PlanSoinDto marquerRealise(Long id, String notes) {
        PlanSoin ps = planSoinRepository.findById(id).orElseThrow(() -> new RuntimeException("Soin non trouvé"));
        ps.setRealise(true);
        ps.setDateRealisation(java.time.LocalDateTime.now());
        if (notes != null) ps.setNotesInfirmier(notes);
        return toDto(planSoinRepository.save(ps));
    }

    @Transactional
    public void delete(Long id) { planSoinRepository.deleteById(id); }

    private PlanSoinDto toDto(PlanSoin ps) {
        String nom = ps.getPatient() != null && ps.getPatient().getUser() != null
            ? ps.getPatient().getUser().getPrenom() + " " + ps.getPatient().getUser().getNom() : "";
        return PlanSoinDto.builder().id(ps.getId()).typeSoin(ps.getTypeSoin()).description(ps.getDescription())
            .dateSoin(ps.getDateSoin()).dateRealisation(ps.getDateRealisation()).realise(ps.isRealise())
            .notesInfirmier(ps.getNotesInfirmier())
            .hospitalisationId(ps.getHospitalisation() != null ? ps.getHospitalisation().getId() : null)
            .patientId(ps.getPatient() != null ? ps.getPatient().getId() : null).patientNom(nom.trim()).build();
    }
}