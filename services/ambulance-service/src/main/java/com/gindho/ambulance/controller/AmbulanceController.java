package com.gindho.ambulance.controller;

import com.gindho.base.ApiResponse;
import com.gindho.ambulance.model.Ambulance;
import com.gindho.ambulance.model.MissionAmbulance;
import com.gindho.ambulance.service.AmbulanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambulances")
@RequiredArgsConstructor
@Slf4j
public class AmbulanceController {

    private final AmbulanceService ambulanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Ambulance>>> findAll() {
        List<Ambulance> ambulances = ambulanceService.findAllAmbulances();
        return ResponseEntity.ok(ApiResponse.of(ambulances));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<ApiResponse<List<Ambulance>>> getDisponibles() {
        List<Ambulance> disponibles = ambulanceService.getAmbulancesDisponibles();
        return ResponseEntity.ok(ApiResponse.ok("Ambulances disponibles", disponibles));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Ambulance>> create(@Valid @RequestBody Ambulance ambulance) {
        Ambulance created = ambulanceService.createAmbulance(ambulance);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Ambulance créée", created));
    }

    @PostMapping("/missions")
    public ResponseEntity<ApiResponse<MissionAmbulance>> assignerMission(@Valid @RequestBody MissionAmbulance mission) {
        MissionAmbulance assigned = ambulanceService.assignerMission(mission);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Mission assignée", assigned));
    }

    @PutMapping("/missions/{id}/complete")
    public ResponseEntity<ApiResponse<MissionAmbulance>> terminerMission(@PathVariable Long id) {
        MissionAmbulance completed = ambulanceService.terminerMission(id);
        return ResponseEntity.ok(ApiResponse.ok("Mission terminée", completed));
    }

    @GetMapping("/missions/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<MissionAmbulance>>> getMissionsPatient(@PathVariable Long patientId) {
        List<MissionAmbulance> missions = ambulanceService.getMissionsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.of(missions));
    }
}