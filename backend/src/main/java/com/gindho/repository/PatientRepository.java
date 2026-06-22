package com.gindho.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gindho.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByNumeroPatient(String numeroPatient);
    Optional<Patient> findByUserEmail(String email);
    Optional<Patient> findByUser_Id(Long userId);

    List<Patient> findByCreeLeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT p FROM Patient p JOIN p.user u WHERE u.nom LIKE %:criteres% OR u.prenom LIKE %:criteres% OR p.numeroPatient LIKE %:criteres%")
    org.springframework.data.domain.Page<Patient> findByNomContainingOrPrenomContainingOrNumeroPatientContaining(
            @Param("criteres") String criteres, 
            org.springframework.data.domain.Pageable pageable);
}
