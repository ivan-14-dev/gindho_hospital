package com.gindho.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gindho.model.Ordonnance;

public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {

    Page<Ordonnance> findByDossierMedical_Patient_Id(Long patientId, Pageable pageable);

    // Filtrage par médecin (dossierMedical -> rendezVous -> medecin)
    Page<Ordonnance> findByDossierMedical_RendezVous_Medecin_Id(Long medecinId, Pageable pageable);

    // Patient + Médecin (dossierMedical->rendezVous->medecin)
    Page<Ordonnance> findByDossierMedical_Patient_IdAndDossierMedical_RendezVous_Medecin_Id(
            Long patientId,
            Long medecinId,
            Pageable pageable
    );

    List<Ordonnance> findByDossierMedical_Patient_IdAndDossierMedical_RendezVous_Id(Long patientId, Long rendezVousId);

    Page<Ordonnance> findByMedicamentContainingIgnoreCase(String medicament, Pageable pageable);
}
