package com.gindho.scheduling.controller;

import com.gindho.base.ApiResponse;
import com.gindho.scheduling.model.Garde;
import com.gindho.scheduling.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GardeAliasController {

    private final SchedulingService schedulingService;

    @GetMapping("/api/gardes/medecin/{employeId}")
    public ResponseEntity<ApiResponse<List<Garde>>> listByMedecin(@PathVariable Long employeId) {
        return ResponseEntity.ok(ApiResponse.of(schedulingService.getGardesByEmploye(employeId, java.time.LocalDate.now().minusDays(30), java.time.LocalDate.now().plusDays(30))));
    }

    @PostMapping("/api/gardes")
    public ResponseEntity<ApiResponse<Garde>> create(@RequestBody Garde garde) {
        return ResponseEntity.ok(ApiResponse.of(schedulingService.planifierGarde(garde)));
    }

    @PatchMapping("/api/gardes/{id}/confirmer")
    public ResponseEntity<ApiResponse<Garde>> confirmer(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(schedulingService.confirmerGarde(id)));
    }
}
