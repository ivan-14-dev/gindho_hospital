package com.gindho.service;
import com.gindho.dto.ExamenImagerieDto;
import com.gindho.model.*;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ImagerieService {
    private final ExamenImagerieRepository examenRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final PatientAccessService patientAccessService;

    @Transactional
    public ExamenImagerieDto create(ExamenImagerieDto dto) {
        if (dto.getPatientId() != null) patientAccessService.assertPatientAccess(dto.getPatientId());
        Patient p = patientRepository.findById(dto.getPatientId()).orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        Medecin m = medecinRepository.findById(dto.getMedecinId()).orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        ExamenImagerie ei = ExamenImagerie.builder().typeExamen(dto.getTypeExamen())
            .dateExamen(dto.getDateExamen() != null ? dto.getDateExamen() : java.time.LocalDateTime.now())
            .compteRendu(dto.getCompteRendu()).fichierDicom(dto.getFichierDicom()).patient(p).medecin(m).build();
        return toDto(examenRepository.save(ei));
    }

    public List<ExamenImagerieDto> listByPatient(Long patientId) {
        patientAccessService.assertPatientAccess(patientId);
        return examenRepository.findByPatientIdOrderByDateExamenDesc(patientId).stream().map(this::toDto).collect(Collectors.toList());
    }

    private ExamenImagerieDto toDto(ExamenImagerie ei) {
        String pn = ei.getPatient() != null && ei.getPatient().getUser() != null ? ei.getPatient().getUser().getPrenom() + " " + ei.getPatient().getUser().getNom() : "";
        String mn = ei.getMedecin() != null && ei.getMedecin().getUser() != null ? ei.getMedecin().getUser().getPrenom() + " " + ei.getMedecin().getUser().getNom() : "";
        return ExamenImagerieDto.builder().id(ei.getId()).typeExamen(ei.getTypeExamen())
            .dateExamen(ei.getDateExamen()).compteRendu(ei.getCompteRendu()).fichierDicom(ei.getFichierDicom())
            .patientId(ei.getPatient().getId()).patientNom(pn.trim())
            .medecinId(ei.getMedecin().getId()).medecinNom(mn.trim()).build();
    }
}