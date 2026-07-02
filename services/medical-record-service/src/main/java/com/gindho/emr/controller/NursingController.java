package com.gindho.emr.controller;

import com.gindho.base.ApiResponse;
import com.gindho.emr.dto.AdministrationMedicamentDto;
import com.gindho.emr.dto.PlanSoinDto;
import com.gindho.emr.dto.SigneVitauxDto;
import com.gindho.emr.service.AdministrationMedicamentService;
import com.gindho.emr.service.PlanSoinService;
import com.gindho.emr.service.SigneVitauxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NursingController {
    private final SigneVitauxService signeVitauxService;
    private final PlanSoinService planSoinService;
    private final AdministrationMedicamentService administrationMedicamentService;

    @GetMapping("/signes-vitaux/patient/{patientId}")
    public ResponseEntity<ApiResponse<Object>> listSignesVitauxPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(signeVitauxService.listByPatient(patientId)));
    }

    @GetMapping("/signes-vitaux/hospitalisation/{hospitalisationId}")
    public ResponseEntity<ApiResponse<Object>> listSignesVitauxHospitalisation(@PathVariable Long hospitalisationId) {
        return ResponseEntity.ok(ApiResponse.of(signeVitauxService.listByHospitalisation(hospitalisationId)));
    }

    @GetMapping("/signes-vitaux")
    public ResponseEntity<ApiResponse<Object>> listAllSignesVitaux() {
        return ResponseEntity.ok(ApiResponse.okList(signeVitauxService.listAll()));
    }

    @PostMapping("/signes-vitaux")
    public ResponseEntity<ApiResponse<SigneVitauxDto>> createSignesVitaux(@RequestBody SigneVitauxDto dto) {
        return ResponseEntity.ok(ApiResponse.of(signeVitauxService.create(dto)));
    }

    @DeleteMapping("/signes-vitaux/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSignesVitaux(@PathVariable Long id) {
        signeVitauxService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Signe vital supprimé", null));
    }

    @GetMapping("/plans-soins/patient/{patientId}")
    public ResponseEntity<ApiResponse<Object>> listPlansSoinsPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(planSoinService.listByPatient(patientId)));
    }

    @GetMapping("/plans-soins/hospitalisation/{hospitalisationId}")
    public ResponseEntity<ApiResponse<Object>> listPlansSoinsHospitalisation(@PathVariable Long hospitalisationId) {
        return ResponseEntity.ok(ApiResponse.of(planSoinService.listByHospitalisation(hospitalisationId)));
    }

    @GetMapping("/plans-soins")
    public ResponseEntity<ApiResponse<Object>> listAllPlansSoins() {
        return ResponseEntity.ok(ApiResponse.okList(planSoinService.listAll()));
    }

    @PostMapping("/plans-soins")
    public ResponseEntity<ApiResponse<PlanSoinDto>> createPlanSoin(@RequestBody PlanSoinDto dto) {
        return ResponseEntity.ok(ApiResponse.of(planSoinService.create(dto)));
    }

    @PatchMapping("/plans-soins/{id}/realiser")
    public ResponseEntity<ApiResponse<PlanSoinDto>> marquerSoinRealise(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.of(planSoinService.marquerRealise(id, notes)));
    }

    @DeleteMapping("/plans-soins/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlanSoin(@PathVariable Long id) {
        planSoinService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Plan de soin supprimé", null));
    }

    @GetMapping("/nursing/assigned-patients")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> assignedPatients() {
        return ResponseEntity.ok(ApiResponse.okList(List.of()));
    }

    @GetMapping("/administrations-medicaments/patient/{patientId}")
    public ResponseEntity<ApiResponse<Object>> listAdminMedPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(administrationMedicamentService.listByPatient(patientId)));
    }

    @PostMapping("/administrations-medicaments")
    public ResponseEntity<ApiResponse<AdministrationMedicamentDto>> createAdminMed(@RequestBody AdministrationMedicamentDto dto) {
        return ResponseEntity.ok(ApiResponse.of(administrationMedicamentService.create(dto)));
    }

    @PatchMapping("/administrations-medicaments/{id}/administrer")
    public ResponseEntity<ApiResponse<AdministrationMedicamentDto>> marquerAdministre(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(administrationMedicamentService.marquerAdministre(id)));
    }
}
