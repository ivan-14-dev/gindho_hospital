package com.gindho.service;
import com.gindho.dto.ProgrammeOperatoireDto;
import com.gindho.model.*;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class ProgrammeOperatoireService {
    private final ProgrammeOperatoireRepository programmeRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    @Transactional
    public ProgrammeOperatoireDto create(ProgrammeOperatoireDto dto) {
        Patient p = patientRepository.findById(dto.getPatientId()).orElseThrow();
        Medecin c = medecinRepository.findById(dto.getChirurgienId()).orElseThrow();
        Medecin a = dto.getAnesthesisteId() != null ? medecinRepository.findById(dto.getAnesthesisteId()).orElse(null) : null;
        ProgrammeOperatoire po = ProgrammeOperatoire.builder().salle(dto.getSalle()).dateDebut(dto.getDateDebut())
            .dateFin(dto.getDateFin()).intervention(dto.getIntervention()).statut("PROGRAMME")
            .patient(p).chirurgien(c).anesthesiste(a).build();
        return toDto(programmeRepository.save(po));
    }

    @Transactional
    public ProgrammeOperatoireDto updateStatut(Long id, String statut) {
        ProgrammeOperatoire po = programmeRepository.findById(id).orElseThrow();
        po.setStatut(statut);
        return toDto(programmeRepository.save(po));
    }

    private ProgrammeOperatoireDto toDto(ProgrammeOperatoire po) {
        return ProgrammeOperatoireDto.builder().id(po.getId()).salle(po.getSalle())
            .dateDebut(po.getDateDebut()).dateFin(po.getDateFin()).intervention(po.getIntervention())
            .statut(po.getStatut()).patientId(po.getPatient().getId()).patientNom(po.getPatient().getUser().getNom())
            .chirurgienId(po.getChirurgien().getId()).chirurgienNom(po.getChirurgien().getUser().getNom())
            .anesthesisteId(po.getAnesthesiste() != null ? po.getAnesthesiste().getId() : null).build();
    }
}