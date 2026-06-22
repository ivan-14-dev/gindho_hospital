package com.gindho.service;
import com.gindho.dto.TeleconsultationDto;
import com.gindho.model.*;
import com.gindho.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional(readOnly=true)
public class TeleconsultationService {
    private final TeleconsultationRepository teleconsultationRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;

    @Transactional
    public TeleconsultationDto create(TeleconsultationDto dto) {
        Patient p = patientRepository.findById(dto.getPatientId()).orElseThrow();
        Medecin m = medecinRepository.findById(dto.getMedecinId()).orElseThrow();
        Teleconsultation t = Teleconsultation.builder().dateDebut(dto.getDateDebut()).statut("PLANIFIEE")
            .lienVideo(dto.getLienVideo()).patient(p).medecin(m).build();
        return toDto(teleconsultationRepository.save(t));
    }

    @Transactional
    public TeleconsultationDto updateStatut(Long id, String statut) {
        Teleconsultation t = teleconsultationRepository.findById(id).orElseThrow();
        t.setStatut(statut);
        if ("TERMINEE".equals(statut)) t.setDateFin(java.time.LocalDateTime.now());
        return toDto(teleconsultationRepository.save(t));
    }

    private TeleconsultationDto toDto(Teleconsultation t) {
        return TeleconsultationDto.builder().id(t.getId()).dateDebut(t.getDateDebut()).dateFin(t.getDateFin())
            .statut(t.getStatut()).lienVideo(t.getLienVideo()).notes(t.getNotes())
            .patientId(t.getPatient().getId()).medecinId(t.getMedecin().getId()).build();
    }
}