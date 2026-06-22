package com.gindho.repository;

import com.gindho.model.SigneVitaux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SigneVitauxRepository extends JpaRepository<SigneVitaux, Long> {
    List<SigneVitaux> findByPatientIdOrderByDateReleveDesc(Long patientId);
    List<SigneVitaux> findByHospitalisationIdOrderByDateReleveDesc(Long hospitalisationId);
    List<SigneVitaux> findByPatientIdAndDateReleveBetweenOrderByDateReleveAsc(
            Long patientId, LocalDateTime debut, LocalDateTime fin);
}
