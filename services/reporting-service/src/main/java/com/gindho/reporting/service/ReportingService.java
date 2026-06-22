package com.gindho.reporting.service;

import com.gindho.reporting.dto.DashboardStatsDto;
import com.gindho.reporting.dto.MedecinDashboardDto;
import com.gindho.reporting.dto.PatientDashboardDto;
import com.gindho.reporting.dto.StatsSeriesDto;
import com.gindho.reporting.model.Rapport;
import com.gindho.reporting.model.StatutRapport;
import com.gindho.reporting.repository.RapportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReportingService {
    private final RapportRepository rapportRepo;

    @Transactional
    public Rapport generate(Rapport r) {
        r.setCode("RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        r.setDateGeneration(LocalDateTime.now());
        r.setStatut(StatutRapport.GENERE);
        log.info("Report generated: {}", r.getCode());
        return rapportRepo.save(r);
    }

    public Map<String, Object> getPatientStats(LocalDate debut, LocalDate fin) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("period", debut + " to " + fin);
        stats.put("totalPatients", 0);
        stats.put("newAdmissions", 0);
        stats.put("discharges", 0);
        return stats;
    }

    public Map<String, Object> getFinancialStats(LocalDate debut, LocalDate fin) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("period", debut + " to " + fin);
        stats.put("totalRevenue", 0);
        stats.put("totalInvoices", 0);
        stats.put("pendingPayments", 0);
        return stats;
    }

    public Map<String, Object> getActivityStats(LocalDate debut, LocalDate fin) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("period", debut + " to " + fin);
        stats.put("consultations", 0);
        stats.put("hospitalizations", 0);
        stats.put("surgeries", 0);
        return stats;
    }

    public DashboardStatsDto getAdminStats() {
        return DashboardStatsDto.builder()
                .totalPatients(0)
                .totalMedecins(0)
                .totalRendezVous(0)
                .rendezVousEnAttente(0)
                .rendezVousAujourdhui(0)
                .totalRevenus(0)
                .details(Map.of("status", "ready"))
                .build();
    }

    public StatsSeriesDto getMetricSeries(String metric, LocalDate from, LocalDate to) {
        LocalDate start = from == null ? LocalDate.now().minusDays(29) : from;
        LocalDate end = to == null ? LocalDate.now() : to;
        List<Map<String, Object>> series = new ArrayList<>();
        long days = ChronoUnit.DAYS.between(start, end);
        for (long i = 0; i <= Math.min(days, 90); i++) {
            LocalDate date = start.plusDays(i);
            Map<String, Object> point = new HashMap<>();
            point.put("date", date);
            point.put("value", 0);
            series.add(point);
        }
        return StatsSeriesDto.builder().metric(metric).series(series).build();
    }

    public PatientDashboardDto getPatientDashboard(Long patientId) {
        return PatientDashboardDto.builder().patientId(patientId).build();
    }

    public MedecinDashboardDto getMedecinDashboard(Long medecinId) {
        return MedecinDashboardDto.builder().medecinId(medecinId).build();
    }
}
