package com.gindho.bed.controller;

import com.gindho.base.ApiResponse;
import com.gindho.bed.dto.ChambreDto;
import com.gindho.bed.dto.LitDto;
import com.gindho.bed.model.Chambre;
import com.gindho.bed.model.Lit;
import com.gindho.bed.service.BedService;
import lombok.RequiredArgsConstructor;
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
public class BedController {
    private final BedService bedService;

    @GetMapping("/api/rooms")
    public ResponseEntity<ApiResponse<Object>> listChambres() { return ResponseEntity.ok(ApiResponse.of(bedService.listChambres())); }

    @PostMapping("/api/rooms")
    public ResponseEntity<ApiResponse<ChambreDto>> createChambre(@RequestBody Chambre chambre) { return ResponseEntity.ok(ApiResponse.of(bedService.createChambre(chambre))); }

    @PutMapping("/api/rooms/{id}")
    public ResponseEntity<ApiResponse<ChambreDto>> updateChambre(@PathVariable Long id, @RequestBody Chambre chambre) { return ResponseEntity.ok(ApiResponse.of(bedService.updateChambre(id, chambre))); }

    @DeleteMapping("/api/rooms/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteChambre(@PathVariable Long id) { bedService.deleteChambre(id); return ResponseEntity.ok(ApiResponse.ok("Chambre supprimée", null)); }

    @GetMapping("/api/beds")
    public ResponseEntity<ApiResponse<Object>> listLits() { return ResponseEntity.ok(ApiResponse.of(bedService.listLits())); }

    @GetMapping("/api/beds/chambre/{chambreId}")
    public ResponseEntity<ApiResponse<Object>> listLitsByChambre(@PathVariable Long chambreId) { return ResponseEntity.ok(ApiResponse.of(bedService.listLitsByChambre(chambreId))); }

    @PostMapping("/api/beds")
    public ResponseEntity<ApiResponse<LitDto>> createLit(@RequestBody Lit lit) { return ResponseEntity.ok(ApiResponse.of(bedService.createLit(lit))); }

    @PutMapping("/api/beds/{id}")
    public ResponseEntity<ApiResponse<LitDto>> updateLit(@PathVariable Long id, @RequestBody Lit lit) { return ResponseEntity.ok(ApiResponse.of(bedService.updateLit(id, lit))); }

    @DeleteMapping("/api/beds/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLit(@PathVariable Long id) { bedService.deleteLit(id); return ResponseEntity.ok(ApiResponse.ok("Lit supprimé", null)); }

    @PutMapping("/api/beds/{id}/assign")
    public ResponseEntity<ApiResponse<Void>> assign(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        bedService.assignerLit(id, body.get("patientId")); return ResponseEntity.ok(ApiResponse.ok("Lit assigne", null));
    }

    @PutMapping("/api/beds/{id}/release")
    public ResponseEntity<ApiResponse<Void>> release(@PathVariable Long id) {
        bedService.libererLit(id); return ResponseEntity.ok(ApiResponse.ok("Lit libere", null));
    }
}
