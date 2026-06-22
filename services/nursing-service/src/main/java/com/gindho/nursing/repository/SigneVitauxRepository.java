package com.gindho.nursing.repository;

import com.gindho.nursing.model.SigneVitaux;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SigneVitauxRepository extends JpaRepository<SigneVitaux, Long> {
    List<SigneVitaux> findByPatientIdOrderByDateReleveDesc(Long patientId);
    List<SigneVitaux> findByHospitalisationIdOrderByDateReleveDesc(Long hospitalisationId);
}