package com.gindho.teleconsultation.service;

import com.gindho.teleconsultation.model.Teleconsultation;
import com.gindho.teleconsultation.repository.TeleconsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TeleconsultationService {

    private final TeleconsultationRepository repository;

    @Transactional
    public Teleconsultation create(Teleconsultation teleconsultation) {
        if (teleconsultation.getDateCreation() == null) {
            teleconsultation.setDateCreation(LocalDateTime.now());
        }
        if (teleconsultation.getStatut() == null) {
            teleconsultation.setStatut("PLANIFIEE");
        }
        return repository.save(teleconsultation);
    }

    @Transactional
    public Teleconsultation updateStatut(Long id, String statut) {
        Teleconsultation teleconsultation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Téléconsultation non trouvée: " + id));
        teleconsultation.setStatut(statut);
        return repository.save(teleconsultation);
    }
}