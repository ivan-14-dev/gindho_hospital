package com.gindho.appointment.controller;

import com.gindho.appointment.dto.RendezVousCancelRequest;
import com.gindho.appointment.dto.RendezVousDto;
import com.gindho.appointment.dto.RendezVousUpdateRequest;
import com.gindho.appointment.dto.StatutUpdateRequest;
import com.gindho.appointment.service.RendezVousService;
import com.gindho.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/appointments", "/api/rendezvous"})
@RequiredArgsConstructor
public class AppointmentController {

    private final RendezVousService rendezVousService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RendezVousDto>>> findAll(
            Pageable pageable,
            @RequestParam(required = false) Long utilisateurId) {
        if (utilisateurId != null) {
            return ResponseEntity.ok(ApiResponse.of(rendezVousService.findByUtilisateurId(utilisateurId, pageable)));
        }
        Page<RendezVousDto> result = rendezVousService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Liste des rendez-vous", result));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<RendezVousDto>>> upcoming(
            @RequestParam(required = false) Long medecinId,
            @RequestParam(required = false) Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(rendezVousService.upcoming(medecinId, patientId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RendezVousDto>> findById(@PathVariable Long id) {
        return rendezVousService.findById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok("Rendez-vous trouvé", dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Rendez-vous non trouvé")));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<RendezVousDto>>> findByPatient(@PathVariable Long patientId) {
        List<RendezVousDto> result = rendezVousService.findByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous du patient", result));
    }

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<List<RendezVousDto>>> findByMedecin(
            @PathVariable Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<RendezVousDto> result = rendezVousService.findByMedecin(medecinId, start, end);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous du médecin", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RendezVousDto>> create(@RequestBody RendezVousDto dto) {
        try {
            RendezVousDto result = rendezVousService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Rendez-vous créé", result));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RendezVousDto>> update(@PathVariable Long id, @RequestBody RendezVousUpdateRequest dto) {
        return rendezVousService.update(id, dto)
                .map(v -> ResponseEntity.ok(ApiResponse.ok("Rendez-vous mis à jour", v)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Rendez-vous non trouvé")));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<ApiResponse<RendezVousDto>> updateStatut(@PathVariable Long id, @RequestBody StatutUpdateRequest dto) {
        return rendezVousService.updateStatut(id, dto == null ? null : dto.getStatut())
                .map(v -> ResponseEntity.ok(ApiResponse.ok("Statut mis à jour", v)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Rendez-vous non trouvé")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @RequestBody(required = false) RendezVousCancelRequest dto) {
        rendezVousService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Rendez-vous supprimé", null));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<RendezVousDto>> confirm(@PathVariable Long id) {
        return rendezVousService.confirm(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok("Rendez-vous confirmé", dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Rendez-vous non trouvé")));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RendezVousDto>> cancel(@PathVariable Long id, @RequestBody(required = false) RendezVousCancelRequest dto) {
        String motif = dto == null ? null : dto.getMotif();
        return rendezVousService.cancelWithReason(id, motif)
                .map(result -> ResponseEntity.ok(ApiResponse.ok("Rendez-vous annulé", result)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Rendez-vous non trouvé")));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<RendezVousDto>> complete(@PathVariable Long id) {
        return rendezVousService.complete(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok("Rendez-vous terminé", dto)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Rendez-vous non trouvé")));
    }
}