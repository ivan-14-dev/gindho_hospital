package com.gindho.repository;
import com.gindho.model.ExamenImagerie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ExamenImagerieRepository extends JpaRepository<ExamenImagerie, Long> {
    List<ExamenImagerie> findByPatientIdOrderByDateExamenDesc(Long patientId);
    List<ExamenImagerie> findByTypeExamen(String type);
}