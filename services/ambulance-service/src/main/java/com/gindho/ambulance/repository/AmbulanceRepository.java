package com.gindho.ambulance.repository;

import com.gindho.ambulance.model.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
    List<Ambulance> findByStatut(Ambulance.StatutAmbulance statut);
}