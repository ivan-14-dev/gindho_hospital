package com.gindho.nursing.service;

import com.gindho.nursing.model.AdministrationMedicament;
import com.gindho.nursing.model.PlanSoin;
import com.gindho.nursing.model.SigneVitaux;
import com.gindho.nursing.repository.AdministrationMedicamentRepository;
import com.gindho.nursing.repository.PlanSoinRepository;
import com.gindho.nursing.repository.SigneVitauxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NursingService {

    private final SigneVitauxRepository signeVitauxRepository;
    private final PlanSoinRepository planSoinRepository;
    private final AdministrationMedicamentRepository adminMedRepository;

    // ===== SIGNES VITAUX =====
    public List<SigneVitaux> listSignesVitauxByPatient(Long patientId) {
        return signeVitauxRepository.findByPatientIdOrderByDateReleveDesc(patientId);
    }

    public List<SigneVitaux> listSignesVitauxByHospitalisation(Long hospitalisationId) {
        return signeVitauxRepository.findByHospitalisationIdOrderByDateReleveDesc(hospitalisationId);
    }

    @Transactional
    public SigneVitaux createSignesVitaux(SigneVitaux signeVitaux) {
        if (signeVitaux.getDateReleve() == null) {
            signeVitaux.setDateReleve(LocalDateTime.now());
        }
        return signeVitauxRepository.save(signeVitaux);
    }

    @Transactional
    public void deleteSignesVitaux(Long id) {
        signeVitauxRepository.deleteById(id);
    }

    // ===== PLANS DE SOINS =====
    public List<PlanSoin> listPlansSoinsByPatient(Long patientId) {
        return planSoinRepository.findByPatientIdOrderByDatePlanificationDesc(patientId);
    }

    public List<PlanSoin> listPlansSoinsByHospitalisation(Long hospitalisationId) {
        return planSoinRepository.findByHospitalisationIdOrderByDatePlanificationDesc(hospitalisationId);
    }

    @Transactional
    public PlanSoin createPlanSoin(PlanSoin planSoin) {
        if (planSoin.getDatePlanification() == null) {
            planSoin.setDatePlanification(LocalDateTime.now());
        }
        return planSoinRepository.save(planSoin);
    }

    @Transactional
    public PlanSoin marquerSoinRealise(Long id, String notes) {
        PlanSoin planSoin = planSoinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan de soins non trouvé: " + id));
        planSoin.setRealise(true);
        planSoin.setDateRealisation(LocalDateTime.now());
        if (notes != null) {
            planSoin.setNotes(notes);
        }
        return planSoinRepository.save(planSoin);
    }

    @Transactional
    public void deletePlanSoin(Long id) {
        planSoinRepository.deleteById(id);
    }

    // ===== ADMINISTRATION MÉDICAMENTS =====
    public List<AdministrationMedicament> listAdminMedByPatient(Long patientId) {
        return adminMedRepository.findByPatientIdOrderByDateProgrammeeDesc(patientId);
    }

    @Transactional
    public AdministrationMedicament createAdminMed(AdministrationMedicament adminMed) {
        if (adminMed.getDateProgrammee() == null) {
            adminMed.setDateProgrammee(LocalDateTime.now());
        }
        return adminMedRepository.save(adminMed);
    }

    @Transactional
    public AdministrationMedicament marquerAdministre(Long id) {
        AdministrationMedicament adminMed = adminMedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administration non trouvée: " + id));
        adminMed.setAdministre(true);
        adminMed.setDateAdministree(LocalDateTime.now());
        return adminMedRepository.save(adminMed);
    }
}