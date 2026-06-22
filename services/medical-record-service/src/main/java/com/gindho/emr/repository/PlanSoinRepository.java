package com.gindho.emr.repository;

import com.gindho.emr.model.PlanSoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanSoinRepository extends JpaRepository<PlanSoin, Long> {
    List<PlanSoin> findByPatientIdOrderByDatePrevuAsc(Long patientId);
    List<PlanSoin> findByHospitalisationIdOrderByDatePrevuAsc(Long hospitalisationId);
}
