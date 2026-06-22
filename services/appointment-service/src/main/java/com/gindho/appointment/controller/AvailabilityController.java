package com.gindho.appointment.controller;

import com.gindho.appointment.dto.DisponibiliteDto;
import com.gindho.appointment.service.DisponibiliteService;
import com.gindho.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final DisponibiliteService disponibiliteService;

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<List<DisponibiliteDto>>> findByMedecin(
            @PathVariable Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<DisponibiliteDto> result = disponibiliteService.findByMedecinAndDateRange(medecinId, start, end);
        return ResponseEntity.ok(ApiResponse.ok("Disponibilités du médecin", result));
    }

    @GetMapping("/medecin")
    public ResponseEntity<ApiResponse<List<DisponibiliteDto>>> findByMedecinQuery(
            @RequestParam Long medecinId) {
        List<DisponibiliteDto> result = disponibiliteService.findByMedecinAndDateRange(medecinId, LocalDate.now(), LocalDate.now().plusDays(30));
        return ResponseEntity.ok(ApiResponse.ok("Disponibilités du médecin", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DisponibiliteDto>> create(@RequestBody DisponibiliteDto dto) {
        DisponibiliteDto result = disponibiliteService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Disponibilité créée", result));
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<DisponibiliteDto>>> createBatch(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureFin,
            @RequestParam(defaultValue = "30") int slotDurationMinutes) {
        List<DisponibiliteDto> result = disponibiliteService.createWeekAvailability(
                medecinId, startDate, heureDebut, heureFin, slotDurationMinutes);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Créneaux de disponibilité créés", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DisponibiliteDto>> update(
            @PathVariable Long id,
            @RequestBody DisponibiliteDto dto) {
        Optional<DisponibiliteDto> result = disponibiliteService.update(id, dto);
        return result
                .map(val -> ResponseEntity.ok(ApiResponse.ok("Disponibilité mise à jour", val)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Disponibilité non trouvée")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        disponibiliteService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Disponibilité supprimée", null));
    }
}