package com.gindho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.model.Hospitalisation;

@Repository
public interface HospitalisationRepository extends JpaRepository<Hospitalisation, Long> {

    Optional<Hospitalisation> findByLitIdAndStatut(Long litId, Hospitalisation.StatutHospitalisation statut);

    List<Hospitalisation> findByPatientIdOrderByDateAdmissionDesc(Long patientId);

    List<Hospitalisation> findByStatut(Hospitalisation.StatutHospitalisation statut);
}
