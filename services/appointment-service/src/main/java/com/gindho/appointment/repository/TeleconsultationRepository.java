package com.gindho.appointment.repository;

import com.gindho.appointment.model.Teleconsultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeleconsultationRepository extends JpaRepository<Teleconsultation, Long> {
    List<Teleconsultation> findByPatientIdOrderByDatePrevuDesc(Long patientId);
    List<Teleconsultation> findByMedecinIdOrderByDatePrevuDesc(Long medecinId);
}
