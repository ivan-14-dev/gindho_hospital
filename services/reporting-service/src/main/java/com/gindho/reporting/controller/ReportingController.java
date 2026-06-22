package com.gindho.reporting.controller;

import com.gindho.base.ApiResponse;
import com.gindho.reporting.dto.DashboardStatsDto;
import com.gindho.reporting.dto.MedecinDashboardDto;
import com.gindho.reporting.dto.PatientDashboardDto;
import com.gindho.reporting.dto.StatsQueryDto;
import com.gindho.reporting.dto.StatsSeriesDto;
import com.gindho.reporting.model.Rapport;
import com.gindho.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportingController {
    private final ReportingService reportingService;

    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<Rapport>> generate(@RequestBody Rapport r) {
        return ResponseEntity.ok(ApiResponse.of(reportingService.generate(r)));
    }

    @GetMapping("/reports/stats/patients")
    public ResponseEntity<ApiResponse<Map<String, Object>>> patientStats(@RequestParam String debut, @RequestParam String fin) {
        return ResponseEntity.ok(ApiResponse.of(reportingService.getPatientStats(LocalDate.parse(debut), LocalDate.parse(fin))));
    }

    @GetMapping("/reports/stats/financial")
    public ResponseEntity<ApiResponse<Map<String, Object>>> financialStats(@RequestParam String debut, @RequestParam String fin) {
        return ResponseEntity.ok(ApiResponse.of(reportingService.getFinancialStats(LocalDate.parse(debut), LocalDate.parse(fin))));
    }

    @GetMapping("/reports/stats/activity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> activityStats(@RequestParam String debut, @RequestParam String fin) {
        return ResponseEntity.ok(ApiResponse.of(reportingService.getActivityStats(LocalDate.parse(debut), LocalDate.parse(fin))));
    }

    @GetMapping("/dashboard/admin/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> adminStats() {
        return ResponseEntity.ok(ApiResponse.of(reportingService.getAdminStats()));
    }

    @GetMapping("/dashboard/admin/stats/{metric}")
    public ResponseEntity<ApiResponse<StatsSeriesDto>> adminMetricSeries(
            @PathVariable String metric,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        LocalDate fromDate = from == null || from.isBlank() || "null".equalsIgnoreCase(from) ? null : LocalDate.parse(from);
        LocalDate toDate = to == null || to.isBlank() || "null".equalsIgnoreCase(to) ? null : LocalDate.parse(to);
        return ResponseEntity.ok(ApiResponse.of(reportingService.getMetricSeries(metric, fromDate, toDate)));
    }

    @PostMapping("/dashboard/admin/stats/query")
    public ResponseEntity<ApiResponse<StatsSeriesDto>> queryAdminStats(@RequestBody StatsQueryDto dto) {
        return ResponseEntity.ok(ApiResponse.of(reportingService.getMetricSeries(dto.getMetric(), dto.getFrom(), dto.getTo())));
    }

    @GetMapping("/dashboard/patient/{patientId}")
    public ResponseEntity<ApiResponse<PatientDashboardDto>> patientDashboard(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(reportingService.getPatientDashboard(patientId)));
    }

    @GetMapping("/dashboard/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<MedecinDashboardDto>> medecinDashboard(@PathVariable Long medecinId) {
        return ResponseEntity.ok(ApiResponse.of(reportingService.getMedecinDashboard(medecinId)));
    }

    @GetMapping("/dashboard/medecin/{medecinId}/patients")
    public ResponseEntity<ApiResponse<java.util.List<?>>> medecinPatients(@PathVariable Long medecinId) {
        return ResponseEntity.ok(ApiResponse.of(java.util.List.of()));
    }
}
