package com.gindho.patient.repository;

import com.gindho.patient.model.IdentifiantPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IdentifiantPatientRepository extends JpaRepository<IdentifiantPatient, Long> {
    List<IdentifiantPatient> findByPatientId(Long patientId);
}