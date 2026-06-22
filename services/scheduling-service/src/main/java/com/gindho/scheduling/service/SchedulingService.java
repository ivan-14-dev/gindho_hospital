package com.gindho.scheduling.service;
import com.gindho.scheduling.model.*; import com.gindho.scheduling.repository.*;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List;
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class SchedulingService {
    private final GardeRepository gardeRepo; private final PlanningRepository planRepo;
    private final AbsenceRepository absRepo;
    public Garde planifierGarde(Garde garde) {
        garde.setStatut(StatutGarde.PLANIFIE);
        log.info("Shift planned: emp={} from {} to {}", garde.getEmployeId(), garde.getDateDebut(), garde.getDateFin());
        return gardeRepo.save(garde);
    }
    @Transactional(readOnly=true) public List<Garde> getGardesByEmploye(Long empId, LocalDate debut, LocalDate fin) {
        return gardeRepo.findByEmployeIdAndDateDebutBetween(empId, debut.atStartOfDay(), fin.atTime(23, 59));
    }
    @Transactional(readOnly=true) public List<Garde> getGardesByService(String service, LocalDate date) {
        return gardeRepo.findByServiceAndDateDebutBetween(service, date.atStartOfDay(), date.atTime(23, 59));
    }
    public Garde findGardeById(Long id) { return gardeRepo.findById(id).orElseThrow(() -> new RuntimeException("Garde non trouvée")); }
    public Garde confirmerGarde(Long id) {
        Garde g = findGardeById(id);
        g.setStatut(StatutGarde.CONFIRME);
        return gardeRepo.save(g);
    }
    @Transactional public Absence declareAbsence(Absence abs) { log.info("Absence declared: emp={} from {} to {}", abs.getEmployeId(), abs.getDateDebut(), abs.getDateFin()); return absRepo.save(abs); }
}