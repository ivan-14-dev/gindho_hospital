package com.gindho.procurement.controller;
import com.gindho.procurement.model.*; import com.gindho.procurement.service.ProcurementService;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/procurement") @RequiredArgsConstructor
public class ProcurementController {
    private final ProcurementService procurementService;
    @GetMapping("/suppliers") public ResponseEntity<ApiResponse<List<Fournisseur>>> getSuppliers() { return ResponseEntity.ok(ApiResponse.of(procurementService.findAll())); }
    @PostMapping("/suppliers") public ResponseEntity<ApiResponse<Fournisseur>> createSupplier(@RequestBody Fournisseur f) { return ResponseEntity.ok(ApiResponse.of(procurementService.createSupplier(f))); }
    @GetMapping("/orders") public ResponseEntity<ApiResponse<List<BonCommande>>> getOrders() { return ResponseEntity.ok(ApiResponse.of(procurementService.findAllOrders())); }
    @PostMapping("/orders") public ResponseEntity<ApiResponse<BonCommande>> createOrder(@RequestBody BonCommande bc) { return ResponseEntity.ok(ApiResponse.of(procurementService.createOrder(bc, List.of()))); }
    @PutMapping("/orders/{id}/receive") public ResponseEntity<ApiResponse<BonCommande>> receive(@PathVariable Long id) { return ResponseEntity.ok(ApiResponse.of(procurementService.receiveDelivery(id))); }
}