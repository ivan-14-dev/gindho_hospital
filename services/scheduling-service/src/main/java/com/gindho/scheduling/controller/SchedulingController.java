package com.gindho.scheduling.controller;
import com.gindho.scheduling.model.*; import com.gindho.scheduling.service.SchedulingService;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.time.LocalDate; import java.util.List;
@RestController @RequestMapping("/api/schedules") @RequiredArgsConstructor
public class SchedulingController {
    private final SchedulingService schedulingService;
    @GetMapping("/gardes") public ResponseEntity<ApiResponse<List<Garde>>> getGardes(
        @RequestParam(required=false) Long employeId, @RequestParam(required=false) String service,
        @RequestParam(required=false) String dateDebut, @RequestParam(required=false) String dateFin) {
        return ResponseEntity.ok(ApiResponse.of(schedulingService.getGardesByEmploye(employeId, LocalDate.now(), LocalDate.now().plusDays(7))));
    }
    @PostMapping("/gardes") public ResponseEntity<ApiResponse<Garde>> createGarde(@RequestBody Garde garde) {
        return ResponseEntity.ok(ApiResponse.of(schedulingService.planifierGarde(garde)));
    }
    @PostMapping("/absences") public ResponseEntity<ApiResponse<Absence>> declareAbsence(@RequestBody Absence abs) {
        return ResponseEntity.ok(ApiResponse.of(schedulingService.declareAbsence(abs)));
    }
}