package com.gindho.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.gindho.dto.PatientMaladieDto;
import com.gindho.model.Analyse;
import com.gindho.model.Maladie;
import com.gindho.model.MaladieAnalyseCritere;
import com.gindho.model.Patient;
import com.gindho.model.PatientMaladie;
import com.gindho.repository.AnalyseRepository;
import com.gindho.repository.MaladieRepository;
import com.gindho.repository.PatientMaladieRepository;
import com.gindho.repository.PatientRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientMaladieService {

    private final AnalyseRepository analyseRepository;
    private final MaladieRepository maladieRepository;
    private final PatientMaladieRepository patientMaladieRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public void recomputeForPatient(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId manquant");

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé id=" + patientId));

        List<Analyse> analyses = analyseRepository
                .findByPatientId(patientId, Pageable.unpaged())
                .getContent();

        List<Maladie> maladiesActives = maladieRepository.findByActifTrue();

        Set<Long> matchedMaladieIds = new HashSet<>();
        for (Maladie maladie : maladiesActives) {
            if (maladieMatch(maladie, analyses)) {
                matchedMaladieIds.add(maladie.getId());
            }
        }

        List<PatientMaladie> existants = patientMaladieRepository.findByPatientId(patientId);

        // 1) Désactiver celles qui ne matchent plus
        for (PatientMaladie pm : existants) {
            if (pm == null || pm.getMaladie() == null) continue;
            Long maladieId = pm.getMaladie().getId();
            boolean shouldBeActive = maladieId != null && matchedMaladieIds.contains(maladieId);
            pm.setActif(shouldBeActive);
            // date_diagnostic reste si on désactive (historique)
        }
        patientMaladieRepository.saveAll(existants);

        // 2) Activer/créer celles qui matchent
        for (Long maladieId : matchedMaladieIds) {
            boolean alreadyActive = existants.stream()
                    .anyMatch(pm -> pm != null
                            && pm.getMaladie() != null
                            && pm.isActif()
                            && maladieId.equals(pm.getMaladie().getId()));

            if (alreadyActive) continue;

            Maladie maladie = maladieRepository.findById(maladieId)
                    .orElseThrow(() -> new RuntimeException("Maladie non trouvée id=" + maladieId));

            PatientMaladie pm = patientMaladieRepository.findByPatientIdAndMaladieId(patientId, maladieId)
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (pm == null) {
                PatientMaladie created = new PatientMaladie();
                created.setPatient(patient);
                created.setMaladie(maladie);
                created.setDateDiagnostic(LocalDate.now());
                created.setMethode("MVP_SYMPTOMES_ANALYSES");
                created.setActif(true);
                patientMaladieRepository.save(created);
            } else {
                pm.setActif(true);
                if (pm.getDateDiagnostic() == null) {
                    pm.setDateDiagnostic(LocalDate.now());
                }
                if (pm.getMethode() == null) {
                    pm.setMethode("MVP_SYMPTOMES_ANALYSES");
                }
                patientMaladieRepository.save(pm);
            }
        }
    }

    private boolean maladieMatch(Maladie maladie, List<Analyse> analyses) {
        if (maladie == null) return false;

        Set<String> symptomesNeed = new HashSet<>();
        if (maladie.getSymptomes() != null) {
            for (String s : maladie.getSymptomes()) {
                if (s == null || s.isBlank()) continue;
                symptomesNeed.add(s.trim().toLowerCase(Locale.ROOT));
            }
        }

        // MVP symptôme: symptôme présent si analyse.resultat contient le symptôme (ignore case)
        for (String symptome : symptomesNeed) {
            boolean found = analyses.stream()
                    .anyMatch(a -> a != null
                            && a.getResultat() != null
                            && a.getResultat().toLowerCase(Locale.ROOT).contains(symptome));
            if (!found) return false;
        }

        // MVP critères analyses: (typeAnalyse, resultatRequis)
        if (maladie.getAnalyseCriteres() != null) {
            for (MaladieAnalyseCritere c : maladie.getAnalyseCriteres()) {
                if (c == null) continue;

                String typeAnalyse = c.getTypeAnalyse();
                if (typeAnalyse == null || typeAnalyse.isBlank()) return false;

                String resultatRequis = c.getResultatRequis();
                if (resultatRequis == null) resultatRequis = "";
                final String resultatRequisNorm = resultatRequis.trim().toLowerCase(Locale.ROOT);
                final String typeAnalyseNorm = typeAnalyse.trim();

                boolean ok = analyses.stream()
                        .anyMatch(a -> a != null
                                && a.getTypeAnalyse() != null
                                && a.getTypeAnalyse().equalsIgnoreCase(typeAnalyseNorm)
                                && a.getResultat() != null
                                && a.getResultat().toLowerCase(Locale.ROOT).contains(resultatRequisNorm));

                if (!ok) return false;
            }
        }

        return true;
    }

    @Transactional(readOnly = true)
    public List<PatientMaladieDto> listByPatient(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId manquant");

        return patientMaladieRepository.findByPatientIdAndActifTrue(patientId).stream()
                .map(this::convertToDto)
                .toList();
    }

    private PatientMaladieDto convertToDto(PatientMaladie pm) {
        if (pm == null) return null;

        PatientMaladieDto dto = new PatientMaladieDto();
        dto.setId(pm.getId());
        dto.setPatientId(pm.getPatient().getId());
        dto.setMaladieId(pm.getMaladie().getId());
        dto.setDateDiagnostic(pm.getDateDiagnostic());
        dto.setMethode(pm.getMethode());
        dto.setActif(pm.isActif());
        return dto;
    }
}
