package com.gindho.pharmacy.repository;

import com.gindho.pharmacy.model.PatientMaladie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientMaladieRepository extends JpaRepository<PatientMaladie, Long> {
    List<PatientMaladie> findByPatientId(Long patientId);
}
