package com.gindho.appointment.repository;

import com.gindho.appointment.model.RendezVous;
import com.gindho.appointment.model.StatutRDV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByPatientIdOrderByDateHeureDebutDesc(Long patientId);

    List<RendezVous> findByMedecinIdAndDateHeureDebutBetween(Long medecinId, LocalDateTime start, LocalDateTime end);

    List<RendezVous> findByDateHeureDebutGreaterThanEqualOrderByDateHeureDebutAsc(java.time.LocalDateTime date);

    List<RendezVous> findByMedecinIdAndDateHeureDebutGreaterThanEqualOrderByDateHeureDebutAsc(Long medecinId, java.time.LocalDateTime date);

    List<RendezVous> findByPatientIdAndDateHeureDebutGreaterThanEqualOrderByDateHeureDebutAsc(Long patientId, java.time.LocalDateTime date);

    Optional<RendezVous> findByIdAndPatientId(Long id, Long patientId);

    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.statut = :statut AND r.dateHeureDebut >= :debut AND r.dateHeureDebut < :fin")
    long countByStatutAndDateBetween(@Param("statut") StatutRDV statut,
                                     @Param("debut") LocalDateTime debut,
                                     @Param("fin") LocalDateTime fin);
}