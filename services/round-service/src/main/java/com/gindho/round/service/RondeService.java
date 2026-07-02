package com.gindho.round.service;
import com.gindho.round.model.*; import com.gindho.round.repository.*;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List;
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class RondeService {
    private final RondeRepository rondeRepo; private final ParticipantRondeRepository partRepo;
    private final ChecklistRondeRepository checkRepo; private final CompteRenduRondeRepository crRepo;
    public Ronde planifierRonde(Ronde ronde, List<Long> participantIds) {
        ronde.setStatut(StatutRonde.PLANIFIEE);
        Ronde saved = rondeRepo.save(ronde);
        for (Long empId : participantIds) {
            ParticipantRonde p = new ParticipantRonde(); p.setRondeId(saved.getId()); p.setEmployeId(empId); p.setPresent(false);
            partRepo.save(p);
        }
        log.info("Round planned: {} for patient {}", saved.getId(), ronde.getPatientId());
        return saved;
    }
    public Ronde demarrerRonde(Long id) { Ronde r = rondeRepo.findById(id).orElseThrow(); r.setStatut(StatutRonde.EN_COURS); return rondeRepo.save(r); }
    public Ronde terminerRonde(Long id, String compteRendu) {
        Ronde r = rondeRepo.findById(id).orElseThrow(); r.setStatut(StatutRonde.TERMINEE);
        rondeRepo.save(r);
        CompteRenduRonde cr = new CompteRenduRonde(); cr.setRondeId(id); cr.setContenu(compteRendu);
        cr.setStatut(StatutCompteRendu.TERMINE); crRepo.save(cr);
        log.info("Round completed: {}", id); return r;
    }
    public ChecklistRonde ajouterChecklist(Long rondeId, ChecklistRonde item) { item.setRondeId(rondeId); return checkRepo.save(item); }
    public void validerChecklist(Long checkId, boolean effectue) { ChecklistRonde c = checkRepo.findById(checkId).orElseThrow(); c.setEffectue(effectue); checkRepo.save(c); }
    @Transactional(readOnly=true) public List<Ronde> getByPatient(Long patientId) { return rondeRepo.findByPatientId(patientId); }
    @Transactional(readOnly=true) public List<Ronde> getByMedecin(Long medId, LocalDate date) {
        return rondeRepo.findByMedecinResponsableIdAndDateRondeBetween(medId, date.atStartOfDay(), date.atTime(23, 59));
    }

    public CompteRenduRonde createAssessment(Long rondeId, String contenu) {
        CompteRenduRonde cr = new CompteRenduRonde();
        cr.setRondeId(rondeId);
        cr.setContenu(contenu);
        cr.setStatut(StatutCompteRendu.TERMINE);
        return crRepo.save(cr);
    }

    @Transactional(readOnly=true) public List<CompteRenduRonde> listAssessments() {
        return crRepo.findAll();
    }

    @Transactional(readOnly=true) public List<CompteRenduRonde> getAssessmentsByRonde(Long rondeId) {
        return crRepo.findByRondeId(rondeId).map(List::of).orElseGet(List::of);
    }
}