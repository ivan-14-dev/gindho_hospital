package com.gindho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.model.PatientMaladie;

@Repository
public interface PatientMaladieRepository extends JpaRepository<PatientMaladie, Long> {

    List<PatientMaladie> findByPatientId(Long patientId);

    List<PatientMaladie> findByPatientIdAndActifTrue(Long patientId);

    List<PatientMaladie> findByPatientIdAndMaladieId(Long patientId, Long maladieId);

    boolean existsByPatientIdAndMaladieId(Long patientId, Long maladieId);
}
