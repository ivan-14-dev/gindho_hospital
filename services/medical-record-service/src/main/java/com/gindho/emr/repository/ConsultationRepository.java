package com.gindho.emr.repository;

import com.gindho.emr.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByPatientIdOrderByDateConsultationDesc(Long patientId);
    List<Consultation> findByMedecinIdOrderByDateConsultationDesc(Long medecinId);
}
