package com.gindho.payment.controller;
import com.gindho.payment.model.*; import com.gindho.payment.service.PaymentService;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
@RestController @RequestMapping({"/api/payments", "/api/paiements"}) @RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping public ResponseEntity<ApiResponse<PaiementTransaction>> pay(@RequestBody PaiementTransaction tx) {
        return ResponseEntity.ok(ApiResponse.of(paymentService.processPayment(tx)));
    }
    @PostMapping("/{id}/refund") public ResponseEntity<ApiResponse<Remboursement>> refund(@PathVariable Long id, @RequestParam BigDecimal montant, @RequestParam String motif) {
        return ResponseEntity.ok(ApiResponse.of(paymentService.refund(id, montant, motif)));
    }
    @GetMapping("/invoice/{invoiceId}") public ResponseEntity<ApiResponse<List<PaiementTransaction>>> getByInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.of(paymentService.getByInvoice(invoiceId)));
    }
    @GetMapping("/patient/{patientId}") public ResponseEntity<ApiResponse<List<PaiementTransaction>>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(paymentService.getByPatient(patientId)));
    }
    @GetMapping public ResponseEntity<ApiResponse<List<PaiementTransaction>>> getAll(@RequestParam(required = false) String statut) {
        if (statut != null && !statut.isBlank()) {
            return ResponseEntity.ok(ApiResponse.of(paymentService.getByStatut(StatutTransaction.valueOf(statut.trim().toUpperCase(java.util.Locale.ROOT)))));
        }
        return ResponseEntity.ok(ApiResponse.of(paymentService.getByStatut(StatutTransaction.REUSSI)));
    }
}