package com.gindho.nursing.repository;

import com.gindho.nursing.model.AdministrationMedicament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdministrationMedicamentRepository extends JpaRepository<AdministrationMedicament, Long> {
    List<AdministrationMedicament> findByPatientIdOrderByDateProgrammeeDesc(Long patientId);
}