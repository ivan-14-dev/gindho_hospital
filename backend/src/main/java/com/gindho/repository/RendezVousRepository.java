package com.gindho.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gindho.model.Patient;
import com.gindho.model.RendezVous;
import com.gindho.model.RendezVous.StatutRDV;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findByMedecinIdAndDateHeureDebutBetween(Long medecinId, LocalDateTime debut, LocalDateTime fin);
    
    List<RendezVous> findByMedecinIdAndStatutNot(Long medecinId, StatutRDV statut);
    
    List<RendezVous> findByPatientId(Long patientId);
    
    List<RendezVous> findByMedecinId(Long medecinId);
    
    List<RendezVous> findByDateHeureDebutBetweenAndStatut(LocalDateTime debut, LocalDateTime fin, StatutRDV statut);
    
    List<RendezVous> findByMedecinIdAndDateHeureDebutBetweenAndStatutNot(
        Long medecinId, LocalDateTime debut, LocalDateTime fin, StatutRDV statut);

    List<RendezVous> findByDateHeureDebutAfterAndStatutNot(LocalDateTime debut, StatutRDV statut);

    List<RendezVous> findByPatientIdAndDateHeureDebutAfterAndStatutNot(Long patientId, LocalDateTime debut, StatutRDV statut);

    List<RendezVous> findByMedecinIdAndDateHeureDebutAfterAndStatutNot(Long medecinId, LocalDateTime debut, StatutRDV statut);

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient p LEFT JOIN FETCH p.user LEFT JOIN FETCH r.medecin m LEFT JOIN FETCH m.user")
    List<RendezVous> findAllWithPatientAndMedecin();

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient p LEFT JOIN FETCH p.user LEFT JOIN FETCH r.medecin m LEFT JOIN FETCH m.user WHERE r.dateHeureDebut >= :debut AND r.statut <> :statut")
    List<RendezVous> findUpcomingWithPatientAndMedecin(LocalDateTime debut, StatutRDV statut);

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient p LEFT JOIN FETCH p.user LEFT JOIN FETCH r.medecin m LEFT JOIN FETCH m.user WHERE r.medecin.id = :medecinId AND r.dateHeureDebut >= :debut AND r.statut <> :statut")
    List<RendezVous> findUpcomingByMedecinWithPatientAndMedecin(Long medecinId, LocalDateTime debut, StatutRDV statut);

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient p LEFT JOIN FETCH p.user LEFT JOIN FETCH r.medecin m LEFT JOIN FETCH m.user WHERE r.patient.id = :patientId AND r.dateHeureDebut >= :debut AND r.statut <> :statut")
    List<RendezVous> findUpcomingByPatientWithPatientAndMedecin(Long patientId, LocalDateTime debut, StatutRDV statut);

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient p LEFT JOIN FETCH p.user LEFT JOIN FETCH r.medecin m LEFT JOIN FETCH m.user WHERE r.patient.id = :patientId")
    List<RendezVous> findByPatientIdWithPatientAndMedecin(Long patientId);

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient p LEFT JOIN FETCH p.user LEFT JOIN FETCH r.medecin m LEFT JOIN FETCH m.user WHERE r.medecin.id = :medecinId AND r.dateHeureDebut BETWEEN :debut AND :fin")
    List<RendezVous> findByMedecinIdAndDateHeureDebutBetweenWithPatientAndMedecin(Long medecinId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT r FROM RendezVous r LEFT JOIN FETCH r.patient p LEFT JOIN FETCH p.user LEFT JOIN FETCH r.medecin m LEFT JOIN FETCH m.user WHERE r.id = :id")
    Optional<RendezVous> findByIdWithPatientAndMedecin(Long id);

    Long countByStatut(StatutRDV statut);
    
    Long countByDateHeureDebutBetween(LocalDateTime debut, LocalDateTime fin);
    
    Long countByPatientIdAndDateHeureDebutAfterAndStatutNot(Long patientId, LocalDateTime debut, StatutRDV statut);
    

    boolean existsByMedecinIdAndPatientId(Long medecinId, Long patientId);

    Long countByMedecinIdAndDateHeureDebutBetween(Long medecinId, LocalDateTime debut, LocalDateTime fin);

    // Patients du médecin (distinct, basé sur les RDV existants)
    @Query("SELECT DISTINCT p FROM RendezVous r JOIN r.patient p JOIN FETCH p.user WHERE r.medecin.id = :medecinId")
    List<Patient> findPatientsByMedecinId(@Param("medecinId") Long medecinId);

    // Compte distinct des patients ayant au moins un RDV avec ce médecin
    @Query("SELECT COUNT(DISTINCT r.patient.id) FROM RendezVous r WHERE r.medecin.id = :medecinId")
    Long countDistinctPatientsByMedecinId(@Param("medecinId") Long medecinId);

    /**
     * Crosstab (côté stats) : nombre de RDV par (statut, médecin) sur une fenêtre temps.
     * Utilisé pour construire une matrice RDV statut × médecin.
     */
    interface RdvStatusMedecinCount {
        Long getMedecinId();
        StatutRDV getStatut();
        Long getCount();
    }

    @Query("SELECT r.medecin.id AS medecinId, r.statut AS statut, COUNT(r) AS count " +
           "FROM RendezVous r " +
           "WHERE r.dateHeureDebut BETWEEN :start AND :endExclusive " +
           "GROUP BY r.medecin.id, r.statut")
    List<RdvStatusMedecinCount> countByStatutAndMedecinBetween(
            @Param("start") LocalDateTime start,
            @Param("endExclusive") LocalDateTime endExclusive
    );
}
