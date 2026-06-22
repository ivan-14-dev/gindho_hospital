package com.gindho.nursing.controller;

import com.gindho.base.ApiResponse;
import com.gindho.nursing.model.SigneVitaux;
import com.gindho.nursing.model.PlanSoin;
import com.gindho.nursing.model.AdministrationMedicament;
import com.gindho.nursing.service.NursingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NursingController {

    private final NursingService nursingService;

    // ===== SIGNES VITAUX =====
    @GetMapping("/api/signes-vitaux/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<SigneVitaux>>> listSignesVitauxPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.okList(nursingService.listSignesVitauxByPatient(patientId)));
    }

    @GetMapping("/api/signes-vitaux/hospitalisation/{hospitalisationId}")
    public ResponseEntity<ApiResponse<List<SigneVitaux>>> listSignesVitauxHospitalisation(@PathVariable Long hospitalisationId) {
        return ResponseEntity.ok(ApiResponse.okList(nursingService.listSignesVitauxByHospitalisation(hospitalisationId)));
    }

    @PostMapping("/api/signes-vitaux")
    public ResponseEntity<ApiResponse<SigneVitaux>> createSignesVitaux(@Valid @RequestBody SigneVitaux signeVitaux) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Signes vitaux enregistrés", nursingService.createSignesVitaux(signeVitaux)));
    }

    @DeleteMapping("/api/signes-vitaux/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSignesVitaux(@PathVariable Long id) {
        nursingService.deleteSignesVitaux(id);
        return ResponseEntity.ok(ApiResponse.ok("Signes vitaux supprimés", null));
    }

    // ===== PLANS DE SOINS =====
    @GetMapping("/api/plans-soins/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<PlanSoin>>> listPlansSoinsPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.okList(nursingService.listPlansSoinsByPatient(patientId)));
    }

    @GetMapping("/api/plans-soins/hospitalisation/{hospitalisationId}")
    public ResponseEntity<ApiResponse<List<PlanSoin>>> listPlansSoinsHospitalisation(@PathVariable Long hospitalisationId) {
        return ResponseEntity.ok(ApiResponse.okList(nursingService.listPlansSoinsByHospitalisation(hospitalisationId)));
    }

    @PostMapping("/api/plans-soins")
    public ResponseEntity<ApiResponse<PlanSoin>> createPlanSoin(@Valid @RequestBody PlanSoin planSoin) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Plan de soins créé", nursingService.createPlanSoin(planSoin)));
    }

    @PatchMapping("/api/plans-soins/{id}/realiser")
    public ResponseEntity<ApiResponse<PlanSoin>> marquerSoinRealise(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.ok("Soin marqué comme réalisé",
                nursingService.marquerSoinRealise(id, notes)));
    }

    @DeleteMapping("/api/plans-soins/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlanSoin(@PathVariable Long id) {
        nursingService.deletePlanSoin(id);
        return ResponseEntity.ok(ApiResponse.ok("Plan de soins supprimé", null));
    }

    // ===== ADMINISTRATION MÉDICAMENTS =====
    @GetMapping("/api/administrations-medicaments/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<AdministrationMedicament>>> listAdminMedPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.okList(nursingService.listAdminMedByPatient(patientId)));
    }

    @PostMapping("/api/administrations-medicaments")
    public ResponseEntity<ApiResponse<AdministrationMedicament>> createAdminMed(
            @Valid @RequestBody AdministrationMedicament adminMed) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Administration programmée", nursingService.createAdminMed(adminMed)));
    }

    @PatchMapping("/api/administrations-medicaments/{id}/administrer")
    public ResponseEntity<ApiResponse<AdministrationMedicament>> marquerAdministre(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Médicament administré",
                nursingService.marquerAdministre(id)));
    }
}