package com.gindho.round.repository;
import com.gindho.round.model.Ronde;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RondeRepository extends JpaRepository<Ronde, Long> {
    List<Ronde> findByPatientId(Long patientId);
    List<Ronde> findByMedecinResponsableIdAndDateRondeBetween(Long medId, LocalDateTime start, LocalDateTime end);
}