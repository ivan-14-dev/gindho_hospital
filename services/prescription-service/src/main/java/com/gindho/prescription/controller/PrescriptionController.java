package com.gindho.prescription.controller;
import com.gindho.prescription.dto.PrescriptionDto;
import com.gindho.prescription.model.*; import com.gindho.prescription.service.PrescriptionService;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map;
@RestController @RequestMapping("/api/prescriptions") @RequiredArgsConstructor
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    @GetMapping public ResponseEntity<ApiResponse<Page<PrescriptionDto>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(prescriptionService.listAll(pageable)));
    }
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<Page<PrescriptionDto>>> getByPatient(@PathVariable Long patientId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(prescriptionService.getByPatientPage(patientId, pageable)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(prescriptionService.getDetail(id)));
    }
    @PostMapping public ResponseEntity<ApiResponse<Ordonnance>> create(@RequestBody Map<String, Object> body) {
        Ordonnance ord = new Ordonnance();
        List<MedicamentPrescrit> meds = List.of();
        return ResponseEntity.ok(ApiResponse.of(prescriptionService.createPrescription(ord, meds)));
    }
    @PutMapping("/{id}") public ResponseEntity<ApiResponse<Ordonnance>> update(@PathVariable Long id, @RequestBody Ordonnance ord) {
        ord.setId(id);
        return ResponseEntity.ok(ApiResponse.of(prescriptionService.createPrescription(ord, List.of())));
    }
    @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        prescriptionService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Supprimé", null));
    }
}