package com.gindho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.model.AssurancePatient;

@Repository
public interface AssurancePatientRepository extends JpaRepository<AssurancePatient, Long> {

    List<AssurancePatient> findByPatientId(Long patientId);

    List<AssurancePatient> findByPatientIdAndActifTrue(Long patientId);

    Optional<AssurancePatient> findByNumeroPolice(String numeroPolice);

    List<AssurancePatient> findByCompagnieContainingIgnoreCase(String compagnie);
}
