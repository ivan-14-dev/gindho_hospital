package com.gindho.ambulance.service;

import com.gindho.ambulance.model.Ambulance;
import com.gindho.ambulance.model.MissionAmbulance;
import com.gindho.ambulance.repository.AmbulanceRepository;
import com.gindho.ambulance.repository.MissionAmbulanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmbulanceService {

    private final AmbulanceRepository ambulanceRepository;
    private final MissionAmbulanceRepository missionRepository;

    // --- Ambulances CRUD ---

    public List<Ambulance> findAllAmbulances() {
        return ambulanceRepository.findAll();
    }

    public Ambulance findAmbulanceById(Long id) {
        return ambulanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ambulance not found with id: " + id));
    }

    @Transactional
    public Ambulance createAmbulance(Ambulance ambulance) {
        if (ambulance.getStatut() == null) {
            ambulance.setStatut(Ambulance.StatutAmbulance.DISPONIBLE);
        }
        return ambulanceRepository.save(ambulance);
    }

    @Transactional
    public Ambulance updateAmbulance(Long id, Ambulance updated) {
        Ambulance existing = findAmbulanceById(id);
        existing.setCode(updated.getCode());
        existing.setImmatriculation(updated.getImmatriculation());
        existing.setType(updated.getType());
        existing.setStatut(updated.getStatut());
        existing.setEquipements(updated.getEquipements());
        existing.setConducteurId(updated.getConducteurId());
        existing.setPersonnel(updated.getPersonnel());
        existing.setDateDerniereMaintenance(updated.getDateDerniereMaintenance());
        existing.setKilometrage(updated.getKilometrage());
        return ambulanceRepository.save(existing);
    }

    // --- Fonctions métier ---

    public List<Ambulance> getAmbulancesDisponibles() {
        return ambulanceRepository.findByStatut(Ambulance.StatutAmbulance.DISPONIBLE);
    }

    @Transactional
    public MissionAmbulance assignerMission(MissionAmbulance mission) {
        Ambulance ambulance = ambulanceRepository.findById(mission.getAmbulanceId())
                .orElseThrow(() -> new RuntimeException("Ambulance not found with id: " + mission.getAmbulanceId()));

        if (ambulance.getStatut() != Ambulance.StatutAmbulance.DISPONIBLE) {
            throw new RuntimeException("Ambulance " + ambulance.getCode() + " is not available (status: " + ambulance.getStatut() + ")");
        }

        // Set ambulance to EN_MISSION
        ambulance.setStatut(Ambulance.StatutAmbulance.EN_MISSION);
        ambulanceRepository.save(ambulance);

        if (mission.getStatut() == null) {
            mission.setStatut(MissionAmbulance.StatutMission.EN_COURS);
        }

        log.info("Mission assigned to ambulance {}: {}", ambulance.getCode(), mission.getTypeMission());
        return missionRepository.save(mission);
    }

    @Transactional
    public MissionAmbulance terminerMission(Long missionId) {
        MissionAmbulance mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        mission.setStatut(MissionAmbulance.StatutMission.TERMINEE);
        mission.setDateArrivee(java.time.LocalDateTime.now());
        missionRepository.save(mission);

        // Libérer l'ambulance
        Ambulance ambulance = ambulanceRepository.findById(mission.getAmbulanceId())
                .orElseThrow(() -> new RuntimeException("Ambulance not found with id: " + mission.getAmbulanceId()));
        ambulance.setStatut(Ambulance.StatutAmbulance.DISPONIBLE);
        ambulanceRepository.save(ambulance);

        log.info("Mission {} completed, ambulance {} liberated", missionId, ambulance.getCode());
        return mission;
    }

    // --- Missions ---

    public List<MissionAmbulance> getMissionsByPatient(Long patientId) {
        return missionRepository.findByPatientIdOrderByDateDepartDesc(patientId);
    }

    public List<MissionAmbulance> findAllMissions() {
        return missionRepository.findAll();
    }

    public MissionAmbulance findMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + id));
    }

    @Transactional
    public Ambulance updatePosition(Long id, Double lat, Double lng) {
        Ambulance ambulance = findAmbulanceById(id);
        ambulance.setLatitude(lat);
        ambulance.setLongitude(lng);
        ambulance.setDerniereMiseAJourPosition(java.time.LocalDateTime.now());
        return ambulanceRepository.save(ambulance);
    }
}