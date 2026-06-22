package com.gindho.billing.controller;

import com.gindho.billing.dto.FactureDto;
import com.gindho.billing.dto.PaiementDto;
import com.gindho.billing.dto.RevenuDto;
import com.gindho.billing.service.FactureService;
import com.gindho.billing.service.PaiementService;
import com.gindho.billing.service.RevenuService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class BillingController {
    private final FactureService factureService;
    private final PaiementService paiementService;
    private final RevenuService revenuService;

    @GetMapping("/api/revenus")
    public ResponseEntity<ApiResponse<Page<RevenuDto>>> getAllRevenus(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(revenuService.findAll(pageable)));
    }

    @GetMapping("/api/revenus/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<RevenuDto>>> getRevenusByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(revenuService.findByPatient(patientId)));
    }

    @GetMapping("/api/revenus/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<List<RevenuDto>>> getRevenusByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(ApiResponse.of(revenuService.findByMedecin(medecinId)));
    }

    @GetMapping("/api/revenus/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenus(@RequestParam String start, @RequestParam String end) {
        return ResponseEntity.ok(ApiResponse.of(revenuService.getTotalRevenus(LocalDateTime.parse(start), LocalDateTime.parse(end))));
    }

    @GetMapping("/api/revenus/{id}")
    public ResponseEntity<ApiResponse<RevenuDto>> getRevenu(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(revenuService.findById(id)));
    }

    @PostMapping("/api/revenus")
    public ResponseEntity<ApiResponse<RevenuDto>> createRevenu(@RequestBody RevenuDto dto) {
        return ResponseEntity.ok(ApiResponse.of(revenuService.create(dto)));
    }

    @PutMapping("/api/revenus/{id}")
    public ResponseEntity<ApiResponse<RevenuDto>> updateRevenu(@PathVariable Long id, @RequestBody RevenuDto dto) {
        return ResponseEntity.ok(ApiResponse.of(revenuService.update(id, dto)));
    }

    @DeleteMapping("/api/revenus/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRevenu(@PathVariable Long id) {
        revenuService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Revenu supprimé", null));
    }

    @GetMapping("/api/invoices")
    public ResponseEntity<ApiResponse<?>> findAll(Pageable p) { return ResponseEntity.ok(ApiResponse.of(factureService.findAll(p))); }

    @GetMapping("/api/invoices/{id}")
    public ResponseEntity<ApiResponse<?>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(factureService.findById(id)));
    }

    @GetMapping("/api/invoices/patient/{patientId}")
    public ResponseEntity<ApiResponse<?>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(factureService.findByPatient(patientId)));
    }

    @PostMapping("/api/invoices")
    public ResponseEntity<ApiResponse<?>> create(@Valid @RequestBody FactureDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(factureService.create(dto)));
    }

    @PostMapping("/api/invoices/{id}/pay")
    public ResponseEntity<ApiResponse<?>> pay(@PathVariable Long id, @Valid @RequestBody PaiementDto dto) {
        return ResponseEntity.ok(ApiResponse.of(factureService.payer(id, dto)));
    }

    @PutMapping("/api/invoices/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        factureService.annuler(id);
        return ResponseEntity.ok(ApiResponse.ok("Cancelled", null));
    }

    @GetMapping("/api/invoices/{id}/payments")
    public ResponseEntity<ApiResponse<?>> payments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(paiementService.findByFacture(id)));
    }
}
