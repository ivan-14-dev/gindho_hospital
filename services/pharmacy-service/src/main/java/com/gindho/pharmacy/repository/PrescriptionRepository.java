package com.gindho.pharmacy.repository;
import com.gindho.pharmacy.model.Prescription; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientIdOrderByDatePrescriptionDesc(Long patientId);
}