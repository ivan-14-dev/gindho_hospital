package com.gindho.prescription.repository;
import com.gindho.prescription.model.Ordonnance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
    List<Ordonnance> findByPatientIdOrderByDatePrescriptionDesc(Long patientId);
    Page<Ordonnance> findByPatientIdOrderByDatePrescriptionDesc(Long patientId, Pageable pageable);
    List<Ordonnance> findByMedecinIdOrderByDatePrescriptionDesc(Long medecinId);
    Page<Ordonnance> findAll(Pageable pageable);
}