package com.gindho.patient.repository;

import com.gindho.patient.model.AssurancePatient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssurancePatientRepository extends JpaRepository<AssurancePatient, Long> {
    List<AssurancePatient> findByPatientId(Long patientId);
}