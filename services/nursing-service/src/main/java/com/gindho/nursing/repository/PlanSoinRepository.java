package com.gindho.nursing.repository;

import com.gindho.nursing.model.PlanSoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanSoinRepository extends JpaRepository<PlanSoin, Long> {
    List<PlanSoin> findByPatientIdOrderByDatePlanificationDesc(Long patientId);
    List<PlanSoin> findByHospitalisationIdOrderByDatePlanificationDesc(Long hospitalisationId);
}