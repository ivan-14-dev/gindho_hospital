package com.gindho.round.controller;
import com.gindho.round.model.*; import com.gindho.round.service.RondeService;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.time.LocalDate; import java.util.List;
@RestController @RequestMapping("/api/rounds") @RequiredArgsConstructor
public class RondeController {
    private final RondeService rondeService;
    @PostMapping public ResponseEntity<ApiResponse<Ronde>> planifier(@RequestBody Ronde ronde, @RequestParam List<Long> participants) {
        return ResponseEntity.ok(ApiResponse.of(rondeService.planifierRonde(ronde, participants)));
    }
    @PutMapping("/{id}/start") public ResponseEntity<ApiResponse<Ronde>> start(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.of(rondeService.demarrerRonde(id))); }
    @PutMapping("/{id}/complete") public ResponseEntity<ApiResponse<Ronde>> complete(@PathVariable Long id, @RequestParam String compteRendu) { return ResponseEntity.ok(ApiResponse.of(rondeService.terminerRonde(id, compteRendu))); }
    @PostMapping("/{id}/checklist") public ResponseEntity<ApiResponse<ChecklistRonde>> addChecklist(@PathVariable Long id, @RequestBody ChecklistRonde item) { return ResponseEntity.ok(ApiResponse.of(rondeService.ajouterChecklist(id, item))); }
    @GetMapping("/patient/{patientId}") public ResponseEntity<ApiResponse<List<Ronde>>> getByPatient(@PathVariable Long patientId) { return ResponseEntity.ok(ApiResponse.of(rondeService.getByPatient(patientId))); }
    @GetMapping("/medecin/{medecinId}") public ResponseEntity<ApiResponse<List<Ronde>>> getByMedecin(@PathVariable Long medecinId, @RequestParam(required=false) String date) {
        return ResponseEntity.ok(ApiResponse.of(rondeService.getByMedecin(medecinId, LocalDate.now())));
    }
}