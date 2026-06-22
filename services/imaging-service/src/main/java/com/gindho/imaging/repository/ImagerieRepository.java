package com.gindho.imaging.repository;
import com.gindho.imaging.model.Imagerie; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ImagerieRepository extends JpaRepository<Imagerie, Long> {
    List<Imagerie> findByPatientIdOrderByDatePrescriptionDesc(Long patientId);
}