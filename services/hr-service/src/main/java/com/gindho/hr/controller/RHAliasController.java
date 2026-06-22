package com.gindho.hr.controller;

import com.gindho.base.ApiResponse;
import com.gindho.hr.dto.EmployeDto;
import com.gindho.hr.dto.PresenceDto;
import com.gindho.hr.model.Employe;
import com.gindho.hr.service.HrService;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RHAliasController {

    private final HrService hrService;
    private final EventProducer eventProducer;

    @GetMapping("/api/rh/personnel")
    public ResponseEntity<ApiResponse<List<EmployeDto>>> listPersonnel() {
        return ResponseEntity.ok(ApiResponse.okList(hrService.findAll()));
    }

    @GetMapping("/api/praticiens")
    public ResponseEntity<ApiResponse<List<EmployeDto>>> listPraticiens() {
        return ResponseEntity.ok(ApiResponse.okList(hrService.findDoctors()));
    }

    @GetMapping("/api/praticiens/{id}")
    public ResponseEntity<ApiResponse<EmployeDto>> getPraticien(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Praticien récupéré", hrService.findById(id)));
    }

    @PostMapping("/api/praticiens")
    public ResponseEntity<ApiResponse<EmployeDto>> createPraticien(@RequestBody Employe e) {
        EmployeDto created = hrService.create(e);
        eventProducer.publish("hr", BaseEvent.builder()
                .eventType("EMPLOYEE_CREATED")
                .source("hr-service")
                .payload(created)
                .build());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Praticien créé", created));
    }

    @PostMapping("/api/rh/personnel")
    public ResponseEntity<ApiResponse<EmployeDto>> createPersonnel(@RequestBody Employe e) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(hrService.create(e)));
    }

    @DeleteMapping("/api/rh/personnel/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePersonnel(@PathVariable Long id) {
        hrService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Employé supprimé", null));
    }

    @GetMapping("/api/presences")
    public ResponseEntity<ApiResponse<List<PresenceDto>>> listPresences(
            @RequestParam(required = false) Long employeId) {
        if (employeId != null) {
            return ResponseEntity.ok(ApiResponse.okList(hrService.listPresences(employeId)));
        }
        return ResponseEntity.ok(ApiResponse.okList(java.util.List.of()));
    }

    @GetMapping("/api/presences/stats/durationAll")
    public ResponseEntity<ApiResponse<Map<String, Object>>> durationAll() {
        return ResponseEntity.ok(ApiResponse.of(Map.of(
                "totalPresences", hrService.listPresences(null).size(),
                "message", "Total des présences tous employés confondus"
        )));
    }

    @GetMapping("/api/presences/stats/durationSingle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> durationSingle(@RequestParam Long employeId) {
        List<PresenceDto> presences = hrService.listPresences(employeId);
        return ResponseEntity.ok(ApiResponse.of(Map.of(
                "employeId", employeId,
                "presenceCount", presences.size(),
                "message", "Présences pour l'employé " + employeId
        )));
    }

    @PostMapping("/api/rh/presence/pointer/{personnelId}")
    public ResponseEntity<ApiResponse<?>> pointerPresence(@PathVariable Long personnelId) {
        return ResponseEntity.ok(ApiResponse.of(hrService.pointer(personnelId)));
    }

    @PostMapping("/api/rh/conges")
    public ResponseEntity<ApiResponse<?>> createConge(@RequestBody com.gindho.hr.model.Conge conge) {
        return ResponseEntity.ok(ApiResponse.of(hrService.createConge(conge)));
    }

    @PatchMapping("/api/rh/conges/{id}/valider")
    public ResponseEntity<ApiResponse<?>> validerConge(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(hrService.valider(id)));
    }
}
