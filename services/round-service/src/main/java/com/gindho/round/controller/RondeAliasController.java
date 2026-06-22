package com.gindho.round.controller;

import com.gindho.base.ApiResponse;
import com.gindho.round.model.Ronde;
import com.gindho.round.service.RondeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RondeAliasController {

    private final RondeService rondeService;

    @PostMapping("/api/rondes")
    public ResponseEntity<ApiResponse<Ronde>> planifier(@RequestBody Ronde ronde) {
        return ResponseEntity.ok(ApiResponse.ok("Ronde planifiée", rondeService.planifierRonde(ronde, java.util.List.of())));
    }

    @PatchMapping("/api/rondes/{id}/valider")
    public ResponseEntity<ApiResponse<Ronde>> valider(
            @PathVariable Long id,
            @RequestParam(required = false) String compteRendu) {
        return ResponseEntity.ok(ApiResponse.ok("Ronde validée",
                rondeService.terminerRonde(id, compteRendu)));
    }
}
