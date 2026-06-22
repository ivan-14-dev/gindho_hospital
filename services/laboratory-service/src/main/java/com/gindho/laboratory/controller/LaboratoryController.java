package com.gindho.laboratory.controller;

import com.gindho.laboratory.dto.AnalyseDto;
import com.gindho.laboratory.service.AnalyseService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
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

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LaboratoryController {
    private final AnalyseService analyseService;

    @GetMapping("/api/analyses")
    public ResponseEntity<ApiResponse<?>> findAll() {
        return ResponseEntity.ok(ApiResponse.of(analyseService.findByPatient(null)));
    }

    @GetMapping("/api/analyses/{id}")
    public ResponseEntity<ApiResponse<?>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(analyseService.findById(id)));
    }

    @GetMapping("/api/analyses/patient/{patientId}")
    public ResponseEntity<ApiResponse<?>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(analyseService.findByPatient(patientId)));
    }

    @GetMapping("/api/analyses/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<?>> findByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(ApiResponse.of(analyseService.findByMedecin(medecinId)));
    }

    @GetMapping("/api/analyses/urgentes")
    public ResponseEntity<ApiResponse<?>> urgentes() {
        return ResponseEntity.ok(ApiResponse.of(analyseService.findUrgent()));
    }

    @PostMapping("/api/analyses")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody AnalyseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(analyseService.create(dto)));
    }

    @PutMapping("/api/analyses/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @RequestBody AnalyseDto dto) {
        return ResponseEntity.ok(ApiResponse.of(analyseService.update(id, dto)));
    }

    @DeleteMapping("/api/analyses/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        analyseService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Analyse supprimée", null));
    }

    @PutMapping("/api/analyses/{id}/result")
    public ResponseEntity<ApiResponse<?>> addResult(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.of(analyseService.ajouterResultat(id, body.get("resultat"))));
    }
}
