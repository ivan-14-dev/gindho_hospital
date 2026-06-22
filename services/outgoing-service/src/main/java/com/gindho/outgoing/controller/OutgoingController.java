package com.gindho.outgoing.controller;

import com.gindho.base.ApiResponse;
import com.gindho.outgoing.dto.EvenementDto;
import com.gindho.outgoing.dto.MedecinDisponibiliteDto;
import com.gindho.outgoing.dto.TransfertPatientDto;
import com.gindho.outgoing.service.OutgoingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outgoing")
@RequiredArgsConstructor
public class OutgoingController {
    private final OutgoingService outgoingService;

    // ========== DISPONIBILITÉS MÉDECINS ==========
    @GetMapping("/doctors/{medecinId}/availability")
    public ResponseEntity<ApiResponse<List<MedecinDisponibiliteDto>>> getDisponibilites(@PathVariable Long medecinId) {
        return ResponseEntity.ok(ApiResponse.ok("Disponibilités récupérées", outgoingService.getDisponibilitesMedecin(medecinId)));
    }

    @PostMapping("/doctors/availability")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MedecinDisponibiliteDto>> creerDisponibilite(@Valid @RequestBody MedecinDisponibiliteDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Disponibilité créée", outgoingService.creerDisponibilite(dto)));
    }

    @DeleteMapping("/doctors/availability/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> supprimerDisponibilite(@PathVariable Long id) {
        outgoingService.supprimerDisponibilite(id);
        return ResponseEntity.ok(ApiResponse.ok("Disponibilité supprimée", null));
    }

    // ========== TRANSFERTS PATIENTS ==========
    @GetMapping("/transfers")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN')")
    public ResponseEntity<ApiResponse<List<TransfertPatientDto>>> getTransferts(@RequestParam(required = false) String statut) {
        if (statut != null) {
            return ResponseEntity.ok(ApiResponse.ok("Transferts filtrés", outgoingService.getTransfertsByStatut(statut)));
        }
        return ResponseEntity.ok(ApiResponse.ok("Transferts en attente", outgoingService.getTransfertsEnAttente()));
    }

    @GetMapping("/transfers/pending")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN')")
    public ResponseEntity<ApiResponse<List<TransfertPatientDto>>> getTransfertsEnAttente() {
        return ResponseEntity.ok(ApiResponse.ok("Transferts en attente", outgoingService.getTransfertsEnAttente()));
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN')")
    public ResponseEntity<ApiResponse<TransfertPatientDto>> creerTransfert(@Valid @RequestBody TransfertPatientDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Transfert créé", outgoingService.creerTransfert(dto)));
    }

    @GetMapping("/transfers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','MEDECIN')")
    public ResponseEntity<ApiResponse<TransfertPatientDto>> getTransfert(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Transfert récupéré", outgoingService.getTransfert(id)));
    }

    @PutMapping("/transfers/{id}/validate/{valideParId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransfertPatientDto>> validerTransfert(@PathVariable Long id, @PathVariable Long valideParId) {
        return ResponseEntity.ok(ApiResponse.ok("Transfert validé", outgoingService.validerTransfert(id, valideParId)));
    }

    @PutMapping("/transfers/{id}/reject/{valideParId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransfertPatientDto>> refuserTransfert(@PathVariable Long id, @PathVariable Long valideParId) {
        return ResponseEntity.ok(ApiResponse.ok("Transfert refusé", outgoingService.refuserTransfert(id, valideParId)));
    }

    @PutMapping("/transfers/{id}/execute")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> effectuerTransfert(@PathVariable Long id) {
        outgoingService.effectuerTransfert(id);
        return ResponseEntity.ok(ApiResponse.ok("Transfert effectué", null));
    }

    // ========== ÉVÉNEMENTS PUBLICS ==========
    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<EvenementDto>>> getEvenementsPublics() {
        return ResponseEntity.ok(ApiResponse.ok("Événements publics", outgoingService.getEvenementsAPublier()));
    }

    @PostMapping("/events")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EvenementDto>> creerEvenement(@Valid @RequestBody EvenementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Événement créé", outgoingService.creerEvenement(dto)));
    }

    @PutMapping("/events/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EvenementDto>> publierEvenement(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Événement publié", outgoingService.publierEvenement(id)));
    }

    // ========== SYNTHÈSE ADMIN ==========
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getDashboard() {
        var transfers = outgoingService.getTransfertsEnAttente();
        var events = outgoingService.getEvenementsAPublier();
        return ResponseEntity.ok(ApiResponse.ok("Synthèse des flux sortants",
                java.util.Map.of("transfertsEnAttente", transfers.size(), "evenementsAPublier", events.size())));
    }
}