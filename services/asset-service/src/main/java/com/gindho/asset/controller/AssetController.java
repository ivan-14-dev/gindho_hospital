package com.gindho.asset.controller;
import com.gindho.asset.model.*; import com.gindho.asset.service.AssetService;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/assets") @RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;
    @GetMapping public ResponseEntity<ApiResponse<List<Equipement>>> findAll() { return ResponseEntity.ok(ApiResponse.of(assetService.findAll())); }
    @GetMapping("/{id}") public ResponseEntity<ApiResponse<Equipement>> findById(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.of(assetService.findById(id))); }
    @PostMapping public ResponseEntity<ApiResponse<Equipement>> create(@RequestBody Equipement e) { return ResponseEntity.ok(ApiResponse.of(assetService.create(e))); }
    @PutMapping("/{id}") public ResponseEntity<ApiResponse<Equipement>> update(@PathVariable Long id, @RequestBody Equipement e) { return ResponseEntity.ok(ApiResponse.of(assetService.update(id, e))); }
    @PostMapping("/{id}/maintenance") public ResponseEntity<ApiResponse<Maintenance>> planMaintenance(@PathVariable Long id, @RequestBody Maintenance m) { return ResponseEntity.ok(ApiResponse.of(assetService.planifierMaintenance(id, m))); }
    @GetMapping("/{id}/maintenances") public ResponseEntity<ApiResponse<List<Maintenance>>> getMaintenances(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.of(assetService.getMaintenances(id))); }
}