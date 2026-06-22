package com.gindho.repository;

import com.gindho.model.DossierMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {
    Optional<DossierMedical> findByPatientId(Long patientId);
    Optional<DossierMedical> findByRendezVousId(Long rendezVousId);
    List<DossierMedical> findAllByPatientId(Long patientId);
}
