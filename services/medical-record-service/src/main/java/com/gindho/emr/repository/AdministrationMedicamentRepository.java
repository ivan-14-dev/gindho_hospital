package com.gindho.emr.repository;

import com.gindho.emr.model.AdministrationMedicament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdministrationMedicamentRepository extends JpaRepository<AdministrationMedicament, Long> {
    List<AdministrationMedicament> findByPatientIdOrderByDatePrevuAsc(Long patientId);
}
