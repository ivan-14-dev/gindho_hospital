package com.gindho.prescription.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gindho.prescription.model.Ordonnance;
public interface PrescriptionRepository extends JpaRepository<Ordonnance, Long> {
    List<Ordonnance> findByPatientIdOrderByDatePrescriptionDesc(Long patientId);
    Page<Ordonnance> findByPatientIdOrderByDatePrescriptionDesc(Long patientId, Pageable pageable);
}