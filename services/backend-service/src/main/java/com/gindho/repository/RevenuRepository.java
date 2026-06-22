package com.gindho.repository;

import com.gindho.model.Revenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface RevenuRepository extends JpaRepository<Revenu, Long> {
    
    Page<Revenu> findByMedecinId(Long medecinId, Pageable pageable);
    
    Page<Revenu> findByPatientId(Long patientId, Pageable pageable);
    
    @Query("SELECT SUM(r.montant) FROM Revenu r WHERE r.dateRevenu BETWEEN :start AND :end")
    BigDecimal sumBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(r.montant) FROM Revenu r WHERE r.typeRevenu = :type AND r.dateRevenu BETWEEN :start AND :end")
    BigDecimal sumByTypeBetween(@Param("type") String type, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(r.montant) FROM Revenu r")
    BigDecimal sumMontant();
    
    Page<Revenu> findByDateRevenuBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<Revenu> findByTypeRevenu(String typeRevenu, Pageable pageable);
}
