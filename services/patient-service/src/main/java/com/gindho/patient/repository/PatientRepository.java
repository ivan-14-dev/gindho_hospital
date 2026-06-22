package com.gindho.patient.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gindho.patient.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    List<Patient> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);
    Optional<Patient> findByNumeroPatientAndActifTrue(String numeroPatient);

    Optional<Patient> findByUserIdAndActifTrue(Long userId);

    Optional<Patient> findByEmailAndActifTrue(String email);

    Optional<Patient> findByIdNumberAndActifTrue(String idNumber);

    boolean existsByIdAndActifTrue(Long id);

    long countByActifTrue();

    long countByActifFalse();

    long countBySexe(Patient.Sexe sexe);

    @Query("select count(distinct c.patientId) from Contact c where c.patientId in (select p.id from Patient p where p.actif = true)")
    long countActivePatientsWithContacts();

    @Query("select count(distinct a.patientId) from AssurancePatient a where a.patientId in (select p.id from Patient p where p.actif = true)")
    long countActivePatientsWithAssurances();

    @Query("select count(distinct d.patientId) from DocumentPatient d where d.patientId in (select p.id from Patient p where p.actif = true)")
    long countActivePatientsWithDocuments();

    @Query("select count(p) from Patient p where p.actif = true and p.creeLe >= :start")
    long countCreatedThisMonth(@Param("start") LocalDateTime start);
}
