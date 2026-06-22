package com.gindho.emergency.repository;
import com.gindho.emergency.model.EmergencyTriage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EmergencyTriageRepository extends JpaRepository<EmergencyTriage, Long> {
    List<EmergencyTriage> findByStatutOrderByDateArriveeAsc(EmergencyTriage.StatutUrgence statut);
}