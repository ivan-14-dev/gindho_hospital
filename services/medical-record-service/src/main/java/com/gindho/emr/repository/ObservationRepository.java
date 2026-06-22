package com.gindho.emr.repository;

import com.gindho.emr.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
    List<Observation> findByConsultationId(Long consultationId);
}
