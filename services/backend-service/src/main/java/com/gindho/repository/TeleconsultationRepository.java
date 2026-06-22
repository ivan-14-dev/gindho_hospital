package com.gindho.repository;
import com.gindho.model.Teleconsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TeleconsultationRepository extends JpaRepository<Teleconsultation, Long> {
    List<Teleconsultation> findByPatientIdOrderByDateDebutDesc(Long patientId);
    List<Teleconsultation> findByMedecinIdOrderByDateDebutDesc(Long medecinId);
}