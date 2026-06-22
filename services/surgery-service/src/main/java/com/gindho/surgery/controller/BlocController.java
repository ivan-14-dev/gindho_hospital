package com.gindho.surgery.controller;

import com.gindho.base.ApiResponse;
import com.gindho.surgery.model.InterventionChirurgicale;
import com.gindho.surgery.repository.InterventionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlocController {

    private final InterventionRepository repository;

    @PostMapping("/api/bloc")
    public ResponseEntity<ApiResponse<InterventionChirurgicale>> createProgramme(@RequestBody InterventionChirurgicale p) {
        p.setDateProgrammee(LocalDateTime.now());
        p.setStatut(InterventionChirurgicale.StatutIntervention.PROGRAMMEE);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Programme créé", repository.save(p)));
    }

    @PatchMapping("/api/bloc/{id}/statut")
    public ResponseEntity<ApiResponse<InterventionChirurgicale>> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        InterventionChirurgicale i = repository.findById(id).orElseThrow();
        i.setStatut(InterventionChirurgicale.StatutIntervention.valueOf(statut.toUpperCase(java.util.Locale.ROOT)));
        return ResponseEntity.ok(ApiResponse.ok("Statut mis à jour", repository.save(i)));
    }
}
