package com.gindho.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.dto.StatsSeriesDto;
import com.gindho.model.Medecin;
import com.gindho.model.Patient;
import com.gindho.model.RendezVous.StatutRDV;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.PatientRepository;
import com.gindho.repository.RendezVousRepository;

@Service
@Transactional(readOnly = true)
public class DashboardStatsService {

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HOUR_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final MedecinRepository medecinRepository;

    public DashboardStatsService(
            PatientRepository patientRepository,
            RendezVousRepository rendezVousRepository,
            MedecinRepository medecinRepository
    ) {
        this.patientRepository = patientRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.medecinRepository = medecinRepository;
    }

    public StatsSeriesDto getMetricSeries(String metric, LocalDate fromDate, LocalDate toDate) {
        if (metric == null || metric.isBlank()) {
            throw new IllegalArgumentException("metric manquant");
        }

        String m = metric.trim().toLowerCase(Locale.ROOT);

        if ("patients-by-day-30".equals(m) || "patients_by_day_30".equals(m)) {
            return patientsByDay(fromDate, toDate, 30, "patients-by-day-30");
        }
        if ("patients-by-day-20".equals(m) || "patients_by_day_20".equals(m)) {
            return patientsByDay(fromDate, toDate, 20, "patients-by-day-20");
        }
        if ("patients-by-hour-20".equals(m) || "patients_by_hour_20".equals(m)) {
            return patientsByHour(fromDate, toDate, 20);
        }
        if ("patients-by-month-20".equals(m) || "patients_by_month_20".equals(m)) {
            return patientsByMonth(fromDate, toDate, 20);
        }
        if ("rdv-status-medecin".equals(m) || "rdv_status_medecin".equals(m)) {
            return rdvStatusMedecin(fromDate, toDate);
        }

        throw new IllegalArgumentException("metric non supporté: " + metric);
    }

    private StatsSeriesDto patientsByDay(LocalDate fromDate, LocalDate toDate, int bucketCount, String metric) {
        LocalDate effectiveTo = toDate != null ? toDate : LocalDate.now();
        LocalDate effectiveFrom = fromDate != null ? fromDate : effectiveTo.minusDays(bucketCount - 1);

        LocalDateTime start = effectiveFrom.atStartOfDay();
        LocalDateTime endExclusive = effectiveTo.plusDays(1).atStartOfDay();

        List<Patient> patients = patientRepository.findByCreeLeBetween(start, endExclusive);

        Map<LocalDate, Double> byDay = new HashMap<>();
        if (patients != null) {
            for (Patient p : patients) {
                if (p == null || p.getCreeLe() == null) continue;
                LocalDate d = p.getCreeLe().toLocalDate();
                byDay.merge(d, 1d, Double::sum);
            }
        }

        List<StatsSeriesDto.StatsPointDto> points = new ArrayList<>();
        for (int i = 0; i < bucketCount; i++) {
            LocalDate d = effectiveFrom.plusDays(i);
            double y = byDay.getOrDefault(d, 0d);

            StatsSeriesDto.StatsPointDto pt = new StatsSeriesDto.StatsPointDto();
            pt.setX(d.format(DAY_FMT));
            pt.setY(y);
            points.add(pt);
        }

        StatsSeriesDto res = new StatsSeriesDto();
        res.setMetric(metric);
        res.setPoints(points);
        return res;
    }

    private StatsSeriesDto patientsByHour(LocalDate fromDate, LocalDate toDate, int bucketCount) {
        // fromDate/toDate sont des LocalDate (jour). On bucket sur des heures.
        LocalDate effectiveTo = toDate != null ? toDate : LocalDate.now();
        LocalDate effectiveFrom = fromDate != null ? fromDate : effectiveTo;

        LocalDateTime endExclusive = effectiveTo.plusDays(1).atStartOfDay();
        LocalDateTime start = endExclusive.minusHours(bucketCount);

        List<Patient> patients = patientRepository.findByCreeLeBetween(start, endExclusive);

        Map<LocalDateTime, Double> byHour = new HashMap<>();
        if (patients != null) {
            for (Patient p : patients) {
                if (p == null || p.getCreeLe() == null) continue;
                LocalDateTime h = p.getCreeLe().withMinute(0).withSecond(0).withNano(0);
                byHour.merge(h, 1d, Double::sum);
            }
        }

        List<StatsSeriesDto.StatsPointDto> points = new ArrayList<>();
        for (int i = 0; i < bucketCount; i++) {
            LocalDateTime h = start.plusHours(i);
            double y = byHour.getOrDefault(h, 0d);

            StatsSeriesDto.StatsPointDto pt = new StatsSeriesDto.StatsPointDto();
            pt.setX(h.format(HOUR_FMT));
            pt.setY(y);
            points.add(pt);
        }

        StatsSeriesDto res = new StatsSeriesDto();
        res.setMetric("patients-by-hour-20");
        res.setPoints(points);
        return res;
    }

    private StatsSeriesDto patientsByMonth(LocalDate fromDate, LocalDate toDate, int bucketCount) {
        java.time.YearMonth effectiveToYM = toDate != null ? java.time.YearMonth.from(toDate) : java.time.YearMonth.now();
        java.time.YearMonth effectiveFromYM = fromDate != null ? java.time.YearMonth.from(fromDate) : effectiveToYM.minusMonths(bucketCount - 1);

        LocalDateTime start = effectiveFromYM.atDay(1).atStartOfDay();
        LocalDateTime endExclusive = effectiveToYM.plusMonths(1).atDay(1).atStartOfDay();

        List<Patient> patients = patientRepository.findByCreeLeBetween(start, endExclusive);

        Map<java.time.YearMonth, Double> byMonth = new HashMap<>();
        if (patients != null) {
            for (Patient p : patients) {
                if (p == null || p.getCreeLe() == null) continue;
                java.time.YearMonth ym = java.time.YearMonth.from(p.getCreeLe().toLocalDate());
                byMonth.merge(ym, 1d, Double::sum);
            }
        }

        List<StatsSeriesDto.StatsPointDto> points = new ArrayList<>();
        for (int i = 0; i < bucketCount; i++) {
            java.time.YearMonth ym = effectiveFromYM.plusMonths(i);
            double y = byMonth.getOrDefault(ym, 0d);

            StatsSeriesDto.StatsPointDto pt = new StatsSeriesDto.StatsPointDto();
            pt.setX(ym.format(MONTH_FMT));
            pt.setY(y);
            points.add(pt);
        }

        StatsSeriesDto res = new StatsSeriesDto();
        res.setMetric("patients-by-month-20");
        res.setPoints(points);
        return res;
    }

    /**
     * Crosstab encoding in series points:
     * - x = "{STATUT}|{MED_NOM}"
     * - y = count
     *
     * To keep the UI readable we limit to top N medecins by total RDV in window.
     */
    private StatsSeriesDto rdvStatusMedecin(LocalDate fromDate, LocalDate toDate) {
        LocalDate effectiveTo = toDate != null ? toDate : LocalDate.now();
        LocalDate effectiveFrom = fromDate != null ? fromDate : effectiveTo.minusDays(29);

        LocalDateTime start = effectiveFrom.atStartOfDay();
        LocalDateTime endExclusive = effectiveTo.plusDays(1).atStartOfDay();

        List<RendezVousRepository.RdvStatusMedecinCount> rows =
                rendezVousRepository.countByStatutAndMedecinBetween(start, endExclusive);

        // medecinId -> total
        Map<Long, Long> totalsByMedecin = new HashMap<>();
        // key: medecinId + '|' + statut -> count
        Map<String, Long> countByKey = new HashMap<>();

        if (rows != null) {
            for (RendezVousRepository.RdvStatusMedecinCount r : rows) {
                if (r == null) continue;
                Long medecinId = r.getMedecinId();
                StatutRDV statut = r.getStatut();
                Long count = r.getCount();

                if (medecinId == null || statut == null || count == null) continue;

                totalsByMedecin.merge(medecinId, count, Long::sum);

                String key = medecinId + "|" + statut.name();
                countByKey.put(key, count);
            }
        }

        // Top N medecins by total count
        int topN = 5;
        List<Long> topMedecinIds = new ArrayList<>(totalsByMedecin.keySet());
        topMedecinIds.sort(Comparator.comparingLong((Long id) -> totalsByMedecin.getOrDefault(id, 0L)).reversed());
        if (topMedecinIds.size() > topN) {
            topMedecinIds = topMedecinIds.subList(0, topN);
        }

        // Build points ordered by statut then medecin
        List<StatsSeriesDto.StatsPointDto> points = new ArrayList<>();
        for (StatutRDV statut : StatutRDV.values()) {
            for (Long medecinId : topMedecinIds) {
                String key = medecinId + "|" + statut.name();
                long yLong = countByKey.getOrDefault(key, 0L);

                String medecinNom = resolveMedecinNom(medecinId);

                StatsSeriesDto.StatsPointDto pt = new StatsSeriesDto.StatsPointDto();
                pt.setX(statut.name() + "|" + medecinNom);
                pt.setY((double) yLong);
                points.add(pt);
            }
        }

        StatsSeriesDto res = new StatsSeriesDto();
        res.setMetric("rdv-status-medecin");
        res.setPoints(points);
        return res;
    }

    private String resolveMedecinNom(Long medecinId) {
        if (medecinId == null) return "N/A";
        try {
            Medecin m = medecinRepository.findById(medecinId).orElse(null);
            if (m == null || m.getUser() == null) return "Medecin-" + medecinId;

            String nom = m.getUser().getNom();
            String prenom = m.getUser().getPrenom();
            String full = ((prenom != null ? prenom : "") + " " + (nom != null ? nom : "")).trim();
            if (full.isBlank()) return "Medecin-" + medecinId;
            return full;
        } catch (Exception e) {
            return "Medecin-" + medecinId;
        }
    }
}
