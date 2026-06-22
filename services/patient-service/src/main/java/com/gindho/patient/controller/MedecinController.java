package com.gindho.patient.controller;

import com.gindho.patient.dto.MedecinDto;
import com.gindho.patient.service.MedecinService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
@Slf4j
public class MedecinController {

    private final MedecinService medecinService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MedecinDto>>> list(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(medecinService.findAll(search, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedecinDto>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Médecin récupéré", medecinService.findById(id)));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<MedecinDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok("Médecin récupéré", medecinService.findByUserId(userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MedecinDto>> create(@Valid @RequestBody MedecinDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Médecin créé", medecinService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedecinDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody MedecinDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Médecin mis à jour", medecinService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        medecinService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Médecin supprimé").build());
    }
}
