package com.gindho.asset.controller;

import com.gindho.base.ApiResponse;
import com.gindho.asset.model.Equipement;
import com.gindho.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EquipementAliasController {

    private final AssetService assetService;

    @GetMapping("/api/equipements")
    public ResponseEntity<ApiResponse<List<Equipement>>> list() {
        return ResponseEntity.ok(ApiResponse.okList(assetService.findAll()));
    }

    @PostMapping("/api/equipements")
    public ResponseEntity<ApiResponse<Equipement>> create(@RequestBody Equipement e) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Équipement créé", assetService.create(e)));
    }
}
