package com.gindho.repository;
import com.gindho.model.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
    List<Ambulance> findByStatut(String statut);
}