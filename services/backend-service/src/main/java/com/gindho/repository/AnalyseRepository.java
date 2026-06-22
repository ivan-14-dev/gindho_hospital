package com.gindho.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gindho.model.Analyse;

public interface AnalyseRepository extends JpaRepository<Analyse, Long> {

    Page<Analyse> findByMedecinId(Long medecinId, Pageable pageable);

    Page<Analyse> findByPatientId(Long patientId, Pageable pageable);

    // Propriétaire (patient + medecin)
    Page<Analyse> findByPatientIdAndMedecinId(Long patientId, Long medecinId, Pageable pageable);

    // Urgences filtrées
    Page<Analyse> findByMedecinIdAndUrgentTrue(Long medecinId, Pageable pageable);

    Page<Analyse> findByPatientIdAndUrgentTrue(Long patientId, Pageable pageable);

    List<Analyse> findByDateAnalyseBetween(LocalDateTime start, LocalDateTime end);

    // Search filtré
    Page<Analyse> findByTypeAnalyseContainingIgnoreCase(String type, Pageable pageable);

    Page<Analyse> findByMedecinIdAndTypeAnalyseContainingIgnoreCase(
            Long medecinId,
            String type,
            Pageable pageable
    );

    Page<Analyse> findByPatientIdAndTypeAnalyseContainingIgnoreCase(
            Long patientId,
            String type,
            Pageable pageable
    );

    // Pour dossier médical complet (analyses rattachées à un RDV via dossierMedical -> rendezVous)
    List<Analyse> findByPatientIdAndDossierMedical_RendezVous_Id(Long patientId, Long rendezVousId);

    @Query("SELECT a FROM Analyse a WHERE a.urgent = true")
    Page<Analyse> findUrgentAnalyses(Pageable pageable);
}
