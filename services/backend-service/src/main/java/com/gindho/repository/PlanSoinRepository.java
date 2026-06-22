package com.gindho.repository;

import com.gindho.model.PlanSoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlanSoinRepository extends JpaRepository<PlanSoin, Long> {
    List<PlanSoin> findByHospitalisationIdOrderByDateSoinAsc(Long hospitalisationId);
    List<PlanSoin> findByPatientIdOrderByDateSoinDesc(Long patientId);
    List<PlanSoin> findByPatientIdAndRealiseFalse(Long patientId);
    List<PlanSoin> findByHospitalisationIdAndRealiseFalse(Long hospitalisationId);
}