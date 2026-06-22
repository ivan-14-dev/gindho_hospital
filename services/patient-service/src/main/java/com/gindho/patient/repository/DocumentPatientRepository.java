package com.gindho.patient.repository;

import com.gindho.patient.model.DocumentPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentPatientRepository extends JpaRepository<DocumentPatient, Long> {
    List<DocumentPatient> findByPatientId(Long patientId);
}