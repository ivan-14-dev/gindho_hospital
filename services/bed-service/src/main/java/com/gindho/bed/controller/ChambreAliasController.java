package com.gindho.bed.controller;

import com.gindho.base.ApiResponse;
import com.gindho.bed.dto.ChambreDto;
import com.gindho.bed.dto.LitDto;
import com.gindho.bed.model.Chambre;
import com.gindho.bed.model.Lit;
import com.gindho.bed.service.BedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChambreAliasController {

    private final BedService bedService;

    @GetMapping("/api/chambres")
    public ResponseEntity<ApiResponse<List<ChambreDto>>> list() {
        return ResponseEntity.ok(ApiResponse.okList(bedService.listChambres()));
    }

    @PostMapping("/api/chambres")
    public ResponseEntity<ApiResponse<ChambreDto>> create(@RequestBody Chambre chambre) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Chambre créée", bedService.createChambre(chambre)));
    }

    @PutMapping("/api/chambres/{id}")
    public ResponseEntity<ApiResponse<ChambreDto>> update(@PathVariable Long id, @RequestBody Chambre chambre) {
        return ResponseEntity.ok(ApiResponse.ok("Chambre mise à jour", bedService.updateChambre(id, chambre)));
    }

    @DeleteMapping("/api/chambres/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bedService.deleteChambre(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Chambre supprimée").build());
    }
}

@RestController
@RequiredArgsConstructor
class LitAliasController {

    private final BedService bedService;

    @GetMapping("/api/lits")
    public ResponseEntity<ApiResponse<List<LitDto>>> list() {
        return ResponseEntity.ok(ApiResponse.okList(bedService.listLits()));
    }

    @GetMapping("/api/lits/chambre/{chambreId}")
    public ResponseEntity<ApiResponse<List<LitDto>>> listByChambre(@PathVariable Long chambreId) {
        return ResponseEntity.ok(ApiResponse.okList(bedService.listLitsByChambre(chambreId)));
    }

    @PostMapping("/api/lits")
    public ResponseEntity<ApiResponse<LitDto>> create(@RequestBody Lit lit) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Lit créé", bedService.createLit(lit)));
    }

    @PutMapping("/api/lits/{id}")
    public ResponseEntity<ApiResponse<LitDto>> update(@PathVariable Long id, @RequestBody Lit lit) {
        return ResponseEntity.ok(ApiResponse.ok("Lit mis à jour", bedService.updateLit(id, lit)));
    }

    @DeleteMapping("/api/lits/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bedService.deleteLit(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Lit supprimé").build());
    }
}
