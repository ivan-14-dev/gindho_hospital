package com.gindho.pharmacy.controller;

import com.gindho.base.ApiResponse;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import com.gindho.pharmacy.model.Prescription;
import com.gindho.pharmacy.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionRepository repository;
    private final EventProducer eventProducer;

    @GetMapping("/api/prescriptions")
    public ResponseEntity<ApiResponse<?>> findAll() {
        return ResponseEntity.ok(ApiResponse.of(repository.findAll()));
    }

    @GetMapping("/api/prescriptions/{id}")
    public ResponseEntity<ApiResponse<?>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Prescription introuvable"))));
    }

    @GetMapping("/api/prescriptions/patient/{patientId}")
    public ResponseEntity<ApiResponse<?>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(repository.findByPatientIdOrderByDatePrescriptionDesc(patientId)));
    }

    @PostMapping("/api/prescriptions")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody Prescription p) {
        repository.save(p);
        eventProducer.publish("medical", BaseEvent.builder().eventType(EventType.PRESCRIPTION_CREATED).source("pharmacy-service").payload(p).build());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(p));
    }

    @PutMapping("/api/prescriptions/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @RequestBody Prescription p) {
        Prescription existing = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Prescription introuvable"));
        existing.setPatientId(p.getPatientId());
        existing.setMedecinId(p.getMedecinId());
        existing.setConsultationId(p.getConsultationId());
        existing.setMedicament(p.getMedicament());
        existing.setPosologie(p.getPosologie());
        existing.setDuree(p.getDuree());
        existing.setInstructions(p.getInstructions());
        existing.setStatut(p.getStatut() == null ? Prescription.StatutPrescription.ACTIVE : p.getStatut());
        Prescription saved = repository.save(existing);
        eventProducer.publish("medical", BaseEvent.builder().eventType(EventType.PRESCRIPTION_CREATED).source("pharmacy-service").payload(saved).build());
        return ResponseEntity.ok(ApiResponse.of(saved));
    }

    @DeleteMapping("/api/prescriptions/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Prescription supprimée", null));
    }
}
