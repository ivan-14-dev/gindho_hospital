package com.gindho.scheduling.repository;
import com.gindho.scheduling.model.Planning; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PlanningRepository extends JpaRepository<Planning, Long> {
    Optional<Planning> findByMoisAndAnneeAndService(int mois, int annee, String service);
}