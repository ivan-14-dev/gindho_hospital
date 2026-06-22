package com.gindho.teleconsultation.repository;

import com.gindho.teleconsultation.model.Teleconsultation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeleconsultationRepository extends JpaRepository<Teleconsultation, Long> {
}