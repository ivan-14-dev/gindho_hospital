package com.gindho.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gindho.dto.AnalyseDto;
import com.gindho.dto.AuthenticationRequest;
import com.gindho.dto.AuthenticationResponse;
import com.gindho.dto.ChambreDto;
import com.gindho.dto.DashboardStatsDto;
import com.gindho.dto.DisponibiliteDto;
import com.gindho.dto.DossierMedicalCompletDto;
import com.gindho.dto.FactureDto;
import com.gindho.dto.ForgotPasswordRequest;
import com.gindho.dto.HospitalisationDto;
import com.gindho.dto.LitDto;
import com.gindho.dto.MaladieDto;
import com.gindho.dto.MedecinDashboardDto;
import com.gindho.dto.MedecinDto;
import com.gindho.dto.MedicamentDto;
import com.gindho.dto.PaiementDto;
import com.gindho.dto.PatientDashboardDto;
import com.gindho.dto.PatientDto;
import com.gindho.dto.PatientMaladieDto;
import com.gindho.dto.PrescriptionDto;
import com.gindho.dto.RendezVousDto;
import com.gindho.dto.ResetPasswordRequest;
import com.gindho.dto.RevenuDto;
import com.gindho.dto.StatsQueryDto;
import com.gindho.dto.StatsSeriesDto;
import com.gindho.service.AnalyseService;
import com.gindho.service.ChambreService;
import com.gindho.service.DisponibiliteService;
import com.gindho.service.DossierMedicalService;
import com.gindho.service.FactureService;
import com.gindho.service.HospitalisationService;
import com.gindho.service.LitService;
import com.gindho.service.MaladieService;
import com.gindho.service.MedecinService;
import com.gindho.service.MedicamentService;
import com.gindho.service.OrdonnanceService;
import com.gindho.service.PaiementService;
import com.gindho.service.PasswordResetService;
import com.gindho.service.PatientMaladieService;
import com.gindho.service.PatientService;
import com.gindho.service.RendezVousService;
import com.gindho.service.RevenuService;
import com.gindho.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final PasswordResetService passwordResetService;

    private final UserService userService;
    private final PatientService patientService;
    private final RendezVousService rendezVousService;
    private final MedecinService medecinService;
    private final AnalyseService analyseService;
    private final RevenuService revenuService;
    private final OrdonnanceService ordonnanceService;
    private final DossierMedicalService dossierMedicalService;
    private final DisponibiliteService disponibiliteService;

    private final com.gindho.service.DashboardStatsService dashboardStatsService;

    private final ChambreService chambreService;
    private final LitService litService;
    private final HospitalisationService hospitalisationService;

    private final FactureService factureService;
    private final PaiementService paiementService;

    private final MaladieService maladieService;
    private final MedicamentService medicamentService;
    private final PatientMaladieService patientMaladieService;

    private final com.gindho.service.AssurancePatientService assurancePatientService;

    // ===== Soins infirmiers =====
    private final com.gindho.service.SigneVitauxService signeVitauxService;
    private final com.gindho.service.PlanSoinService planSoinService;
    private final com.gindho.service.AdministrationMedicamentService administrationMedicamentService;

    // ===== Pharmacie =====
    private final com.gindho.service.PharmacieStockService pharmacieStockService;

    // ===== Plannings =====
    private final com.gindho.service.GardeService gardeService;

    // ===== Stocks =====
    private final com.gindho.service.StockConsommableService stockConsommableService;
    // ===== Modules 7-13 =====
    private final com.gindho.service.EvenementService evenementService;
    private final com.gindho.service.RondeMedicaleService rondeMedicaleService;
    private final com.gindho.service.ProgrammeOperatoireService programmeOperatoireService;
    private final com.gindho.service.AuditQualiteService auditQualiteService;
    private final com.gindho.service.IncidentService incidentService;
    private final com.gindho.service.EquipementService equipementService;
    private final com.gindho.service.AmbulanceService ambulanceService;
    private final com.gindho.service.ImagerieService imagerieService;
    private final com.gindho.service.TeleconsultationService teleconsultationService;

    // ===== RH =====
    private final com.gindho.service.PersonnelService personnelService;
    private final com.gindho.service.PresenceService presenceService;
    private final com.gindho.service.CongeService congeService;


    // ===== AUTH =====
    @PostMapping("/auth/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        if (request == null || request.email() == null || request.email().isBlank()) {
            // anti-enumération: toujours OK
            return ResponseEntity.ok().build();
        }
        passwordResetService.sendForgotPasswordOtp(request.email().trim());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Requête manquante");
        }

        passwordResetService.resetPasswordWithOtp(
                request.email() != null ? request.email().trim() : null,
                request.code(),
                request.newPassword()
        );

        return ResponseEntity.ok().build();
    }

    // ===== PATIENTS =====
    @GetMapping("/patients")
    public ResponseEntity<Page<PatientDto>> searchPatients(
            @RequestParam(required = false) String nom,
            Pageable pageable) {
        return ResponseEntity.ok(patientService.rechercher(nom, pageable));
    }

    @PostMapping("/patients")
    public ResponseEntity<PatientDto> createPatient(@RequestBody PatientDto dto) {
        return ResponseEntity.ok(patientService.creer(dto));
    }

    @PutMapping("/patients/{id}")
    public ResponseEntity<PatientDto> updatePatient(
            @PathVariable Long id, @RequestBody PatientDto dto) {
        return ResponseEntity.ok(patientService.mettreAJour(id, dto));
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<PatientDto> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.findById(id));
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patients/{id}/dossier")
    public ResponseEntity<PatientDto> getDossier(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getDossier(id));
    }

    @GetMapping("/patients/{id}/dossier-complet")
    public ResponseEntity<DossierMedicalCompletDto> getDossierComplet(@PathVariable Long id) {
        return ResponseEntity.ok(dossierMedicalService.getDossierMedicalComplet(id));
    }

    // ===== PATIENT (courant) =====
    @GetMapping("/patients/me")
    public ResponseEntity<Long> getCurrentPatientId() {
        return ResponseEntity.ok(patientService.getCurrentPatientIdFromJwt());
    }

    // ===== MEDECINS =====
    @GetMapping("/medecins")
    public ResponseEntity<Page<MedecinDto>> getAllMedecins(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(medecinService.searchMedecins(search, pageable));
        }
        return ResponseEntity.ok(medecinService.getAllMedecins(pageable));
    }

    @GetMapping("/medecins/{id}")
    public ResponseEntity<MedecinDto> getMedecin(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.getMedecinById(id));
    }

    @GetMapping("/medecins/by-user/{userId}")
    public ResponseEntity<MedecinDto> getMedecinByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(medecinService.getMedecinByUserId(userId));
    }

    @PostMapping("/medecins")
    public ResponseEntity<MedecinDto> createMedecin(@RequestBody MedecinDto dto) {
        return ResponseEntity.ok(medecinService.createMedecin(dto));
    }

    @PutMapping("/medecins/{id}")
    public ResponseEntity<MedecinDto> updateMedecin(
            @PathVariable Long id, @RequestBody MedecinDto dto) {
        return ResponseEntity.ok(medecinService.updateMedecin(id, dto));
    }

    @DeleteMapping("/medecins/{id}")
    public ResponseEntity<Void> deleteMedecin(@PathVariable Long id) {
        medecinService.deleteMedecin(id);
        return ResponseEntity.ok().build();
    }

    // ===== RENDEZ-VOUS =====
    @GetMapping("/rendezvous")
    public ResponseEntity<List<RendezVousDto>> getAllRendezVous() {
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }

    @GetMapping("/rendezvous/upcoming")
    public ResponseEntity<List<RendezVousDto>> getUpcomingRendezVous(
            @RequestParam(required = false) Long medecinId,
            @RequestParam(required = false) Long patientId) {
        if (medecinId != null) {
            return ResponseEntity.ok(rendezVousService.getUpcomingByMedecin(medecinId));
        } else if (patientId != null) {
            return ResponseEntity.ok(rendezVousService.getUpcomingByPatient(patientId));
        }
        return ResponseEntity.ok(rendezVousService.getAllUpcoming());
    }

    @GetMapping("/rendezvous/{id}")
    public ResponseEntity<RendezVousDto> getRendezVous(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getRendezVousById(id));
    }

    @PostMapping("/rendezvous")
    public ResponseEntity<RendezVousDto> createRendezVous(@RequestBody RendezVousDto dto) {
        return ResponseEntity.ok(rendezVousService.prendreRDV(dto));
    }

    @PutMapping("/rendezvous/{id}")
    public ResponseEntity<RendezVousDto> updateRendezVous(
            @PathVariable Long id, @RequestBody RendezVousDto dto) {
        return ResponseEntity.ok(rendezVousService.updateRendezVous(id, dto));
    }

    @PatchMapping("/rendezvous/{id}/statut")
    public ResponseEntity<RendezVousDto> updateStatut(
            @PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(rendezVousService.updateStatut(id, statut));
    }

    @DeleteMapping("/rendezvous/{id}")
    public ResponseEntity<Void> cancelRendezVous(
            @PathVariable Long id, @RequestParam String motif) {
        rendezVousService.annuler(id, motif);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rendezvous/patient/{patientId}")
    public ResponseEntity<List<RendezVousDto>> getRendezVousByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(rendezVousService.getRDVsPatient(patientId));
    }

    @GetMapping("/rendezvous/medecin/{medecinId}")
    public ResponseEntity<List<RendezVousDto>> getRendezVousByMedecin(
            @PathVariable Long medecinId, @RequestParam String date) {
        return ResponseEntity.ok(rendezVousService.getRDVsMedecin(
                medecinId,
                java.time.LocalDateTime.parse(date)
        ));
    }

    // ===== DISPONIBILITES =====
    @GetMapping("/disponibilites")
    public ResponseEntity<List<DisponibiliteDto>> listDisponibilitesByMedecin(@RequestParam Long medecinId) {
        return ResponseEntity.ok(disponibiliteService.listByMedecin(medecinId));
    }

    @PostMapping("/disponibilites")
    public ResponseEntity<DisponibiliteDto> createDisponibilite(@RequestBody DisponibiliteDto dto) {
        return ResponseEntity.ok(disponibiliteService.create(dto));
    }

    @PutMapping("/disponibilites/{id}")
    public ResponseEntity<DisponibiliteDto> updateDisponibilite(
            @PathVariable Long id,
            @RequestBody DisponibiliteDto dto) {
        return ResponseEntity.ok(disponibiliteService.update(id, dto));
    }

    @DeleteMapping("/disponibilites/{id}")
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Long id) {
        disponibiliteService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ===== ANALYSES =====
    @GetMapping("/analyses")
    public ResponseEntity<Page<AnalyseDto>> getAllAnalyses(Pageable pageable) {
        return ResponseEntity.ok(analyseService.getAllAnalyses(pageable));
    }

    @GetMapping("/analyses/patient/{patientId}")
    public ResponseEntity<Page<AnalyseDto>> getAnalysesByPatient(
            @PathVariable Long patientId, Pageable pageable) {
        return ResponseEntity.ok(analyseService.getAnalysesByPatient(patientId, pageable));
    }

    @GetMapping("/analyses/medecin/{medecinId}")
    public ResponseEntity<Page<AnalyseDto>> getAnalysesByMedecin(
            @PathVariable Long medecinId, Pageable pageable) {
        return ResponseEntity.ok(analyseService.getAnalysesByMedecin(medecinId, pageable));
    }

    @GetMapping("/analyses/urgentes")
    public ResponseEntity<Page<AnalyseDto>> getUrgentAnalyses(Pageable pageable) {
        return ResponseEntity.ok(analyseService.getUrgentAnalyses(pageable));
    }

    @GetMapping("/analyses/{id}")
    public ResponseEntity<AnalyseDto> getAnalyse(@PathVariable Long id) {
        return ResponseEntity.ok(analyseService.getAnalyseById(id));
    }

    @PostMapping("/analyses")
    public ResponseEntity<AnalyseDto> createAnalyse(@RequestBody AnalyseDto dto) {
        return ResponseEntity.ok(analyseService.createAnalyse(dto));
    }

    @PutMapping("/analyses/{id}")
    public ResponseEntity<AnalyseDto> updateAnalyse(
            @PathVariable Long id, @RequestBody AnalyseDto dto) {
        return ResponseEntity.ok(analyseService.updateAnalyse(id, dto));
    }

    @DeleteMapping("/analyses/{id}")
    public ResponseEntity<Void> deleteAnalyse(@PathVariable Long id) {
        analyseService.deleteAnalyse(id);
        return ResponseEntity.ok().build();
    }

    // ===== MALADIES / MEDICAMENTS =====
    @GetMapping("/maladies")
    public ResponseEntity<List<MaladieDto>> listMaladies() {
        return ResponseEntity.ok(maladieService.list());
    }

    @PostMapping("/maladies")
    public ResponseEntity<MaladieDto> createMaladie(@RequestBody MaladieDto dto) {
        return ResponseEntity.ok(maladieService.create(dto));
    }

    @PutMapping("/maladies/{id}")
    public ResponseEntity<MaladieDto> updateMaladie(@PathVariable Long id, @RequestBody MaladieDto dto) {
        return ResponseEntity.ok(maladieService.update(id, dto));
    }

    @DeleteMapping("/maladies/{id}")
    public ResponseEntity<Void> deleteMaladie(@PathVariable Long id) {
        maladieService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/medicaments")
    public ResponseEntity<List<MedicamentDto>> listMedicaments() {
        return ResponseEntity.ok(medicamentService.list());
    }

    @PostMapping("/medicaments")
    public ResponseEntity<MedicamentDto> createMedicament(@RequestBody MedicamentDto dto) {
        return ResponseEntity.ok(medicamentService.create(dto));
    }

    @PutMapping("/medicaments/{id}")
    public ResponseEntity<MedicamentDto> updateMedicament(@PathVariable Long id, @RequestBody MedicamentDto dto) {
        return ResponseEntity.ok(medicamentService.update(id, dto));
    }

    @DeleteMapping("/medicaments/{id}")
    public ResponseEntity<Void> deleteMedicament(@PathVariable Long id) {
        medicamentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/maladies/patient/{patientId}")
    public ResponseEntity<List<PatientMaladieDto>> listMaladiesPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientMaladieService.listByPatient(patientId));
    }

    // ===== REVENUS =====
    @GetMapping("/revenus")
    public ResponseEntity<Page<RevenuDto>> getAllRevenus(Pageable pageable) {
        return ResponseEntity.ok(revenuService.getAllRevenus(pageable));
    }

    @GetMapping("/revenus/patient/{patientId}")
    public ResponseEntity<Page<RevenuDto>> getRevenusByPatient(
            @PathVariable Long patientId, Pageable pageable) {
        return ResponseEntity.ok(revenuService.getRevenusByPatient(patientId, pageable));
    }

    @GetMapping("/revenus/medecin/{medecinId}")
    public ResponseEntity<Page<RevenuDto>> getRevenusByMedecin(
            @PathVariable Long medecinId, Pageable pageable) {
        return ResponseEntity.ok(revenuService.getRevenusByMedecin(medecinId, pageable));
    }

    @GetMapping("/revenus/total")
    public ResponseEntity<BigDecimal> getTotalRevenus(
            @RequestParam String start, @RequestParam String end) {
        LocalDateTime startDt = LocalDateTime.parse(start);
        LocalDateTime endDt = LocalDateTime.parse(end);
        return ResponseEntity.ok(revenuService.getTotalRevenus(startDt, endDt));
    }

    @GetMapping("/revenus/{id}")
    public ResponseEntity<RevenuDto> getRevenu(@PathVariable Long id) {
        return ResponseEntity.ok(revenuService.getRevenuById(id));
    }

    @PostMapping("/revenus")
    public ResponseEntity<RevenuDto> createRevenu(@RequestBody RevenuDto dto) {
        return ResponseEntity.ok(revenuService.createRevenu(dto));
    }

    @PutMapping("/revenus/{id}")
    public ResponseEntity<RevenuDto> updateRevenu(
            @PathVariable Long id, @RequestBody RevenuDto dto) {
        return ResponseEntity.ok(revenuService.updateRevenu(id, dto));
    }

    @DeleteMapping("/revenus/{id}")
    public ResponseEntity<Void> deleteRevenu(@PathVariable Long id) {
        revenuService.deleteRevenu(id);
        return ResponseEntity.ok().build();
    }

    // ===== FACTURES / PAIEMENTS =====

    @GetMapping("/factures/patient/{patientId}")
    public ResponseEntity<List<FactureDto>> getFacturesByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) String statut
    ) {
        com.gindho.model.Facture.StatutFacture st = null;
        if (statut != null && !statut.isBlank()) {
            st = com.gindho.model.Facture.StatutFacture.valueOf(
                    statut.trim().toUpperCase(java.util.Locale.ROOT)
            );
        }
        return ResponseEntity.ok(factureService.getFacturesByPatient(patientId, st));
    }

    @GetMapping("/factures")
    public ResponseEntity<List<com.gindho.dto.FactureDto>> getFactures(
            @RequestParam(required = false) String statut) {
        if (statut != null && !statut.isBlank()) {
            var st = com.gindho.model.Facture.StatutFacture.valueOf(
                    statut.trim().toUpperCase(java.util.Locale.ROOT)
            );
            return ResponseEntity.ok(factureService.getFacturesByStatut(st));
        }
        return ResponseEntity.ok(factureService.getFacturesByStatut(
                com.gindho.model.Facture.StatutFacture.IMPAYEE
        ));
    }

    @PostMapping("/factures")
    public ResponseEntity<FactureDto> createFacture(@RequestBody FactureDto dto) {
        return ResponseEntity.ok(factureService.createFacture(dto));
    }

    @GetMapping("/paiements/patient/{patientId}")
    public ResponseEntity<List<PaiementDto>> getPaiementsByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) String statut
    ) {
        com.gindho.model.Paiement.StatutPaiement st = null;
        if (statut != null && !statut.isBlank()) {
            st = com.gindho.model.Paiement.StatutPaiement.valueOf(
                    statut.trim().toUpperCase(java.util.Locale.ROOT)
            );
        }
        return ResponseEntity.ok(paiementService.getPaiementsByPatient(patientId, st));
    }

    @GetMapping("/paiements")
    public ResponseEntity<List<PaiementDto>> getPaiements(
            @RequestParam(required = false) String statut) {
        if (statut != null && !statut.isBlank()) {
            var st = com.gindho.model.Paiement.StatutPaiement.valueOf(
                    statut.trim().toUpperCase(java.util.Locale.ROOT)
            );
            return ResponseEntity.ok(paiementService.getPaiementsByStatut(st));
        }
        return ResponseEntity.ok(paiementService.getPaiementsByStatut(
                com.gindho.model.Paiement.StatutPaiement.EN_ATTENTE
        ));
    }

    @PostMapping("/paiements")
    public ResponseEntity<PaiementDto> createPaiement(@RequestBody PaiementDto dto) {
        return ResponseEntity.ok(paiementService.createPaiement(dto));
    }

    // ===== PRESCRIPTIONS =====
    @GetMapping("/prescriptions")
    public ResponseEntity<Page<com.gindho.dto.PrescriptionDto>> getAllPrescriptions(Pageable pageable) {
        return ResponseEntity.ok(ordonnanceService.getAllPrescriptions(pageable));
    }

    @GetMapping("/prescriptions/patient/{patientId}")
    public ResponseEntity<Page<com.gindho.dto.PrescriptionDto>> getPrescriptionsByPatient(
            @PathVariable Long patientId, Pageable pageable) {
        return ResponseEntity.ok(ordonnanceService.getPrescriptionsByPatient(patientId, pageable));
    }

    @GetMapping("/prescriptions/{id}")
    public ResponseEntity<PrescriptionDto> getPrescription(@PathVariable Long id) {
        return ResponseEntity.ok(ordonnanceService.getPrescriptionById(id));
    }

    @PostMapping("/prescriptions")
    public ResponseEntity<PrescriptionDto> createPrescription(@RequestBody PrescriptionDto dto) {
        return ResponseEntity.ok(ordonnanceService.createPrescription(dto));
    }

    @PutMapping("/prescriptions/{id}")
    public ResponseEntity<PrescriptionDto> updatePrescription(
            @PathVariable Long id, @RequestBody PrescriptionDto dto) {
        return ResponseEntity.ok(ordonnanceService.updatePrescription(id, dto));
    }

    @DeleteMapping("/prescriptions/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        ordonnanceService.deletePrescription(id);
        return ResponseEntity.ok().build();
    }

    // ===== ASSURANCES PATIENT =====
    @GetMapping("/assurances/patient/{patientId}")
    public ResponseEntity<List<com.gindho.dto.AssurancePatientDto>> listAssurancesPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(assurancePatientService.listByPatient(patientId));
    }

    @GetMapping("/assurances/patient/{patientId}/actives")
    public ResponseEntity<List<com.gindho.dto.AssurancePatientDto>> listAssurancesActivesPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(assurancePatientService.listActivesByPatient(patientId));
    }

    @GetMapping("/assurances/{id}")
    public ResponseEntity<com.gindho.dto.AssurancePatientDto> getAssurance(@PathVariable Long id) {
        return ResponseEntity.ok(assurancePatientService.getById(id));
    }

    @PostMapping("/assurances")
    public ResponseEntity<com.gindho.dto.AssurancePatientDto> createAssurance(
            @RequestBody com.gindho.dto.AssurancePatientDto dto) {
        return ResponseEntity.ok(assurancePatientService.create(dto));
    }

    @PutMapping("/assurances/{id}")
    public ResponseEntity<com.gindho.dto.AssurancePatientDto> updateAssurance(
            @PathVariable Long id,
            @RequestBody com.gindho.dto.AssurancePatientDto dto) {
        return ResponseEntity.ok(assurancePatientService.update(id, dto));
    }

    @DeleteMapping("/assurances/{id}")
    public ResponseEntity<Void> deleteAssurance(@PathVariable Long id) {
        assurancePatientService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/assurances/recherche")
    public ResponseEntity<List<com.gindho.dto.AssurancePatientDto>> searchAssurances(
            @RequestParam String compagnie) {
        return ResponseEntity.ok(assurancePatientService.searchByCompagnie(compagnie));
    }

    // ===== DASHBOARD ADMIN =====
    @GetMapping("/dashboard/admin/stats")
    public ResponseEntity<DashboardStatsDto> getAdminStats() {
        return ResponseEntity.ok(userService.getAdminStats());
    }

    @GetMapping("/dashboard/admin/stats/{metric}")
    public ResponseEntity<StatsSeriesDto> getAdminMetricSeries(
            @PathVariable String metric,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        java.time.LocalDate fromDate = null;
        java.time.LocalDate toDate = null;

        if (from != null && !from.isBlank() && !"null".equalsIgnoreCase(from)) {
            fromDate = java.time.LocalDate.parse(from.trim());
        }
        if (to != null && !to.isBlank() && !"null".equalsIgnoreCase(to)) {
            toDate = java.time.LocalDate.parse(to.trim());
        }

        return ResponseEntity.ok(dashboardStatsService.getMetricSeries(metric, fromDate, toDate));
    }

    @PostMapping("/dashboard/admin/stats/query")
    public ResponseEntity<StatsSeriesDto> queryAdminStats(
            @RequestBody StatsQueryDto dto) {

        return ResponseEntity.ok(
                dashboardStatsService.getMetricSeries(
                        dto.getMetric(),
                        dto.getFrom(),
                        dto.getTo()
                )
        );
    }

    // ===== DASHBOARD PATIENT =====
    @GetMapping("/dashboard/patient/{patientId}")
    public ResponseEntity<PatientDashboardDto> getPatientDashboard(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientService.getDashboard(patientId));
    }

    // ===== DASHBOARD MEDECIN =====
    @GetMapping("/dashboard/medecin/{medecinId}")
    public ResponseEntity<MedecinDashboardDto> getMedecinDashboard(@PathVariable Long medecinId) {
        return ResponseEntity.ok(medecinService.getDashboard(medecinId));
    }

    @GetMapping("/dashboard/medecin/{medecinId}/patients")
    public ResponseEntity<List<PatientDto>> getPatientsByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(rendezVousService.getPatientsByMedecin(medecinId));
    }

    // ===== SIGNES VITAUX (Soins infirmiers) =====
    @GetMapping("/signes-vitaux/patient/{patientId}")
    public ResponseEntity<List<com.gindho.dto.SigneVitauxDto>> listSignesVitauxPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(signeVitauxService.listByPatient(patientId));
    }

    @GetMapping("/signes-vitaux/hospitalisation/{hospitalisationId}")
    public ResponseEntity<List<com.gindho.dto.SigneVitauxDto>> listSignesVitauxHospitalisation(@PathVariable Long hospitalisationId) {
        return ResponseEntity.ok(signeVitauxService.listByHospitalisation(hospitalisationId));
    }

    @PostMapping("/signes-vitaux")
    public ResponseEntity<com.gindho.dto.SigneVitauxDto> createSignesVitaux(@RequestBody com.gindho.dto.SigneVitauxDto dto) {
        return ResponseEntity.ok(signeVitauxService.create(dto));
    }

    @DeleteMapping("/signes-vitaux/{id}")
    public ResponseEntity<Void> deleteSignesVitaux(@PathVariable Long id) {
        signeVitauxService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ===== PLANS DE SOINS (Soins infirmiers) =====
    @GetMapping("/plans-soins/patient/{patientId}")
    public ResponseEntity<List<com.gindho.dto.PlanSoinDto>> listPlansSoinsPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(planSoinService.listByPatient(patientId));
    }

    @GetMapping("/plans-soins/hospitalisation/{hospitalisationId}")
    public ResponseEntity<List<com.gindho.dto.PlanSoinDto>> listPlansSoinsHospitalisation(@PathVariable Long hospitalisationId) {
        return ResponseEntity.ok(planSoinService.listByHospitalisation(hospitalisationId));
    }

    @PostMapping("/plans-soins")
    public ResponseEntity<com.gindho.dto.PlanSoinDto> createPlanSoin(@RequestBody com.gindho.dto.PlanSoinDto dto) {
        return ResponseEntity.ok(planSoinService.create(dto));
    }

    @PatchMapping("/plans-soins/{id}/realiser")
    public ResponseEntity<com.gindho.dto.PlanSoinDto> marquerSoinRealise(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(planSoinService.marquerRealise(id, notes));
    }

    @DeleteMapping("/plans-soins/{id}")
    public ResponseEntity<Void> deletePlanSoin(@PathVariable Long id) {
        planSoinService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ===== ADMINISTRATION MÉDICAMENTS (Soins infirmiers) =====
    @GetMapping("/administrations-medicaments/patient/{patientId}")
    public ResponseEntity<List<com.gindho.dto.AdministrationMedicamentDto>> listAdminMedPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(administrationMedicamentService.listByPatient(patientId));
    }

    @PostMapping("/administrations-medicaments")
    public ResponseEntity<com.gindho.dto.AdministrationMedicamentDto> createAdminMed(
            @RequestBody com.gindho.dto.AdministrationMedicamentDto dto) {
        return ResponseEntity.ok(administrationMedicamentService.create(dto));
    }

    @PatchMapping("/administrations-medicaments/{id}/administrer")
    public ResponseEntity<com.gindho.dto.AdministrationMedicamentDto> marquerAdministre(@PathVariable Long id) {
        return ResponseEntity.ok(administrationMedicamentService.marquerAdministre(id));
    }

    // ===== PHARMACIE =====
    @GetMapping("/pharmacie")
    public ResponseEntity<List<com.gindho.dto.PharmacieStockDto>> listPharmacie() {
        return ResponseEntity.ok(pharmacieStockService.list());
    }

    @PostMapping("/pharmacie")
    public ResponseEntity<com.gindho.dto.PharmacieStockDto> createPharmacie(@RequestBody com.gindho.dto.PharmacieStockDto dto) {
        return ResponseEntity.ok(pharmacieStockService.create(dto));
    }

    @PatchMapping("/pharmacie/{id}/quantite")
    public ResponseEntity<com.gindho.dto.PharmacieStockDto> updateQuantitePharmacie(
            @PathVariable Long id,
            @RequestParam int quantite) {
        return ResponseEntity.ok(pharmacieStockService.updateQuantite(id, quantite));
    }

    @GetMapping("/pharmacie/recherche")
    public ResponseEntity<List<com.gindho.dto.PharmacieStockDto>> searchPharmacie(@RequestParam String medicament) {
        return ResponseEntity.ok(pharmacieStockService.search(medicament));
    }

    // ===== GARDES (Plannings) =====
    @GetMapping("/gardes/medecin/{medecinId}")
    public ResponseEntity<List<com.gindho.dto.GardeDto>> listGardesMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(gardeService.listByMedecin(medecinId));
    }

    @PostMapping("/gardes")
    public ResponseEntity<com.gindho.dto.GardeDto> createGarde(@RequestBody com.gindho.dto.GardeDto dto) {
        return ResponseEntity.ok(gardeService.create(dto));
    }

    @PatchMapping("/gardes/{id}/confirmer")
    public ResponseEntity<com.gindho.dto.GardeDto> confirmerGarde(@PathVariable Long id) {
        return ResponseEntity.ok(gardeService.confirmer(id));
    }

    // ===== STOCKS CONSOMMABLES =====
    @GetMapping("/stocks")
    public ResponseEntity<List<com.gindho.dto.StockConsommableDto>> listStocks() {
        return ResponseEntity.ok(stockConsommableService.list());
    }

    @GetMapping("/stocks/alertes/rupture")
    public ResponseEntity<List<com.gindho.dto.StockConsommableDto>> alerterRupture() {
        return ResponseEntity.ok(stockConsommableService.alerterRupture());
    }

    @GetMapping("/stocks/alertes/peremption")
    public ResponseEntity<List<com.gindho.dto.StockConsommableDto>> alerterPeremption() {
        return ResponseEntity.ok(stockConsommableService.alerterPeremption());
    }

    @PostMapping("/stocks")
    public ResponseEntity<com.gindho.dto.StockConsommableDto> createStock(@RequestBody com.gindho.dto.StockConsommableDto dto) {
        return ResponseEntity.ok(stockConsommableService.create(dto));
    }


    // ===== RH — PERSONNEL =====
    @GetMapping("/rh/personnel")
    public ResponseEntity<java.util.List<com.gindho.dto.PersonnelDto>> listPersonnel() {
        return ResponseEntity.ok(personnelService.list());
    }

    @PostMapping("/rh/personnel")
    public ResponseEntity<com.gindho.dto.PersonnelDto> createPersonnel(@RequestBody com.gindho.dto.PersonnelDto dto) {
        return ResponseEntity.ok(personnelService.create(dto));
    }

    @DeleteMapping("/rh/personnel/{id}")
    public ResponseEntity<Void> deletePersonnel(@PathVariable Long id) {
        personnelService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ===== RH — PRÉSENCE =====
    @PostMapping("/rh/presence/pointer/{personnelId}")
    public ResponseEntity<com.gindho.dto.PresenceDto> pointerPresence(@PathVariable Long personnelId) {
        return ResponseEntity.ok(presenceService.pointer(personnelId));
    }

    @GetMapping("/rh/presence/{personnelId}")
    public ResponseEntity<java.util.List<com.gindho.dto.PresenceDto>> listPresences(@PathVariable Long personnelId) {
        return ResponseEntity.ok(presenceService.listByPersonnel(personnelId));
    }

    // ===== RH — CONGÉ =====
    @PostMapping("/rh/conges")
    public ResponseEntity<com.gindho.dto.CongeDto> createConge(@RequestBody com.gindho.dto.CongeDto dto) {
        return ResponseEntity.ok(congeService.create(dto));
    }

    @PatchMapping("/rh/conges/{id}/valider")
    public ResponseEntity<com.gindho.dto.CongeDto> validerConge(@PathVariable Long id) {
        return ResponseEntity.ok(congeService.valider(id));
    }

    
    // ===== ÉVÉNEMENTS =====
    @GetMapping("/evenements")
    public ResponseEntity<java.util.List<com.gindho.dto.EvenementDto>> listEvenements() {
        return ResponseEntity.ok(evenementService.list());
    }
    @PostMapping("/evenements")
    public ResponseEntity<com.gindho.dto.EvenementDto> createEvenement(@RequestBody com.gindho.dto.EvenementDto dto) {
        return ResponseEntity.ok(evenementService.create(dto));
    }
    @PatchMapping("/evenements/{id}/valider")
    public ResponseEntity<com.gindho.dto.EvenementDto> validerEvenement(@PathVariable Long id) {
        return ResponseEntity.ok(evenementService.valider(id));
    }

    // ===== RONDES MÉDICALES =====
    @PostMapping("/rondes")
    public ResponseEntity<com.gindho.dto.RondeMedicaleDto> createRonde(@RequestBody com.gindho.dto.RondeMedicaleDto dto) {
        return ResponseEntity.ok(rondeMedicaleService.create(dto));
    }
    @PatchMapping("/rondes/{id}/valider")
    public ResponseEntity<com.gindho.dto.RondeMedicaleDto> validerRonde(@PathVariable Long id, @RequestParam(required=false) String compteRendu) {
        return ResponseEntity.ok(rondeMedicaleService.valider(id, compteRendu));
    }

    // ===== BLOC OPÉRATOIRE =====
    @PostMapping("/bloc")
    public ResponseEntity<com.gindho.dto.ProgrammeOperatoireDto> createProgramme(@RequestBody com.gindho.dto.ProgrammeOperatoireDto dto) {
        return ResponseEntity.ok(programmeOperatoireService.create(dto));
    }
    @PatchMapping("/bloc/{id}/statut")
    public ResponseEntity<com.gindho.dto.ProgrammeOperatoireDto> updateStatutBloc(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(programmeOperatoireService.updateStatut(id, statut));
    }

    // ===== QUALITÉ =====
    @GetMapping("/qualite/audits")
    public ResponseEntity<java.util.List<com.gindho.dto.AuditQualiteDto>> listAudits() {
        return ResponseEntity.ok(auditQualiteService.list());
    }
    @PostMapping("/qualite/audits")
    public ResponseEntity<com.gindho.dto.AuditQualiteDto> createAudit(@RequestBody com.gindho.dto.AuditQualiteDto dto) {
        return ResponseEntity.ok(auditQualiteService.create(dto));
    }

    // ===== INCIDENTS =====
    @GetMapping("/incidents/non-resolus")
    public ResponseEntity<java.util.List<com.gindho.dto.IncidentDto>> listIncidentsNonResolus() {
        return ResponseEntity.ok(incidentService.listNonResolus());
    }
    @PostMapping("/incidents")
    public ResponseEntity<com.gindho.dto.IncidentDto> createIncident(@RequestBody com.gindho.dto.IncidentDto dto) {
        return ResponseEntity.ok(incidentService.create(dto));
    }
    @PatchMapping("/incidents/{id}/resoudre")
    public ResponseEntity<com.gindho.dto.IncidentDto> resoudreIncident(@PathVariable Long id, @RequestParam(required=false) String action) {
        return ResponseEntity.ok(incidentService.resoudre(id, action));
    }

    // ===== ÉQUIPEMENTS =====
    @GetMapping("/equipements")
    public ResponseEntity<java.util.List<com.gindho.dto.EquipementDto>> listEquipements() {
        return ResponseEntity.ok(equipementService.list());
    }
    @PostMapping("/equipements")
    public ResponseEntity<com.gindho.dto.EquipementDto> createEquipement(@RequestBody com.gindho.dto.EquipementDto dto) {
        return ResponseEntity.ok(equipementService.create(dto));
    }

    // ===== AMBULANCES =====
    @GetMapping("/ambulances")
    public ResponseEntity<java.util.List<com.gindho.dto.AmbulanceDto>> listAmbulances() {
        return ResponseEntity.ok(ambulanceService.list());
    }
    @PostMapping("/ambulances")
    public ResponseEntity<com.gindho.dto.AmbulanceDto> createAmbulance(@RequestBody com.gindho.dto.AmbulanceDto dto) {
        return ResponseEntity.ok(ambulanceService.create(dto));
    }
    @PatchMapping("/ambulances/{id}/position")
    public ResponseEntity<com.gindho.dto.AmbulanceDto> updatePositionAmbulance(@PathVariable Long id,
        @RequestParam Double lat, @RequestParam Double lng) {
        return ResponseEntity.ok(ambulanceService.updatePosition(id, lat, lng));
    }
    

    // ===== IMAGERIE =====
    @PostMapping("/imagerie")
    public ResponseEntity<com.gindho.dto.ExamenImagerieDto> createExamenImagerie(@RequestBody com.gindho.dto.ExamenImagerieDto dto) {
        return ResponseEntity.ok(imagerieService.create(dto));
    }
    @GetMapping("/imagerie/patient/{patientId}")
    public ResponseEntity<java.util.List<com.gindho.dto.ExamenImagerieDto>> listImageriePatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(imagerieService.listByPatient(patientId));
    }

    // ===== TÉLÉCONSULTATION =====
    @PostMapping("/teleconsultations")
    public ResponseEntity<com.gindho.dto.TeleconsultationDto> createTeleconsultation(@RequestBody com.gindho.dto.TeleconsultationDto dto) {
        return ResponseEntity.ok(teleconsultationService.create(dto));
    }
    @PatchMapping("/teleconsultations/{id}/statut")
    public ResponseEntity<com.gindho.dto.TeleconsultationDto> updateStatutTeleconsultation(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(teleconsultationService.updateStatut(id, statut));
    }
    // ===== HOSPITALISATION / CHAMBRES / LITS (ADMIN) =====
    @GetMapping("/chambres")
    public ResponseEntity<List<ChambreDto>> listChambres() {
        return ResponseEntity.ok(chambreService.list());
    }

    @PostMapping("/chambres")
    public ResponseEntity<ChambreDto> createChambre(@RequestBody ChambreDto dto) {
        return ResponseEntity.ok(chambreService.create(dto));
    }

    @PutMapping("/chambres/{id}")
    public ResponseEntity<ChambreDto> updateChambre(@PathVariable Long id, @RequestBody ChambreDto dto) {
        return ResponseEntity.ok(chambreService.update(id, dto));
    }

    @DeleteMapping("/chambres/{id}")
    public ResponseEntity<Void> deleteChambre(@PathVariable Long id) {
        chambreService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lits")
    public ResponseEntity<List<LitDto>> listLits() {
        return ResponseEntity.ok(litService.list());
    }

    @GetMapping("/lits/chambre/{chambreId}")
    public ResponseEntity<List<LitDto>> listLitsByChambre(@PathVariable Long chambreId) {
        return ResponseEntity.ok(litService.listByChambre(chambreId));
    }

    @PostMapping("/lits")
    public ResponseEntity<LitDto> createLit(@RequestBody LitDto dto) {
        return ResponseEntity.ok(litService.create(dto));
    }

    @PutMapping("/lits/{id}")
    public ResponseEntity<LitDto> updateLit(@PathVariable Long id, @RequestBody LitDto dto) {
        return ResponseEntity.ok(litService.update(id, dto));
    }

    @DeleteMapping("/lits/{id}")
    public ResponseEntity<Void> deleteLit(@PathVariable Long id) {
        litService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/hospitalisations")
    public ResponseEntity<HospitalisationDto> hospitaliser(@RequestBody HospitalisationDto dto) {
        return ResponseEntity.ok(hospitalisationService.hospitaliser(dto));
    }

    @PatchMapping("/hospitalisations/{id}/rapport-sortie")
    public ResponseEntity<HospitalisationDto> rapportSortie(
            @PathVariable Long id,
            @RequestBody HospitalisationDto dto) {
        return ResponseEntity.ok(hospitalisationService.rapportSortie(id, dto));
    }

    @GetMapping("/hospitalisations/en-cours")
    public ResponseEntity<List<HospitalisationDto>> getHospitalisationsEnCours() {
        return ResponseEntity.ok(hospitalisationService.getHospitalisationsEnCours());
    }

    @GetMapping("/hospitalisations/patient/{patientId}")
    public ResponseEntity<List<HospitalisationDto>> getHospitalisationsParPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(hospitalisationService.getHospitalisationsParPatient(patientId));
    }

    @GetMapping("/hospitalisations")
    public ResponseEntity<List<HospitalisationDto>> getAllHospitalisations() {
        return ResponseEntity.ok(hospitalisationService.getAllHospitalisations());
    }

    @PutMapping("/hospitalisations/{id}")
    public ResponseEntity<HospitalisationDto> updateHospitalisation(
            @PathVariable Long id, @RequestBody HospitalisationDto dto) {
        return ResponseEntity.ok(hospitalisationService.updateHospitalisation(id, dto));
    }

    @DeleteMapping("/hospitalisations/{id}")
    public ResponseEntity<Void> deleteHospitalisation(@PathVariable Long id) {
        hospitalisationService.deleteHospitalisation(id);
        return ResponseEntity.ok().build();
    }
}
