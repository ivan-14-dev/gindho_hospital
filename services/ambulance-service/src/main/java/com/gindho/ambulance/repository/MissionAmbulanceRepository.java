package com.gindho.ambulance.repository;

import com.gindho.ambulance.model.MissionAmbulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionAmbulanceRepository extends JpaRepository<MissionAmbulance, Long> {
    List<MissionAmbulance> findByAmbulanceIdOrderByDateDepartDesc(Long ambulanceId);
    List<MissionAmbulance> findByPatientIdOrderByDateDepartDesc(Long patientId);
    List<MissionAmbulance> findByStatut(MissionAmbulance.StatutMission statut);
}