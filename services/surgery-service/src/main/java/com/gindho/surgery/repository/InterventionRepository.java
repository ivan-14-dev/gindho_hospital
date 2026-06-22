package com.gindho.surgery.repository;
import com.gindho.surgery.model.InterventionChirurgicale;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface InterventionRepository extends JpaRepository<InterventionChirurgicale, Long> {
    List<InterventionChirurgicale> findByPatientIdOrderByDateProgrammeeDesc(Long patientId);
}