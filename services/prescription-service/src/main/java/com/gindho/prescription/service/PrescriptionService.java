package com.gindho.prescription.service;
import com.gindho.prescription.model.Ordonnance;
import com.gindho.prescription.model.MedicamentPrescrit;
import com.gindho.prescription.model.StatutOrdonnance;
import com.gindho.prescription.repository.OrdonnanceRepository;
import com.gindho.prescription.repository.MedicamentPrescritRepository;
import com.gindho.prescription.dto.PrescriptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime; import java.util.*; import java.util.stream.Collectors;
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class PrescriptionService {
    private final OrdonnanceRepository ordRepo;
    private final MedicamentPrescritRepository medRepo;
    public Ordonnance createPrescription(Ordonnance ord, List<MedicamentPrescrit> meds) {
        ord.setStatut(StatutOrdonnance.ACTIVE);
        ord.setDatePrescription(LocalDateTime.now());
        Ordonnance saved = ordRepo.save(ord);
        for (MedicamentPrescrit m : meds) { m.setOrdonnanceId(saved.getId()); }
        medRepo.saveAll(meds);
        log.info("Prescription created: {} for patient {}", saved.getId(), ord.getPatientId());
        return saved;
    }
    @Transactional(readOnly=true) public List<Ordonnance> getByPatient(Long patientId) { return ordRepo.findByPatientIdOrderByDatePrescriptionDesc(patientId); }
    @Transactional(readOnly=true) public Map<String, Object> getDetail(Long ordonnanceId) {
        Ordonnance ord = ordRepo.findById(ordonnanceId).orElse(null);
        List<MedicamentPrescrit> meds = medRepo.findByOrdonnanceId(ordonnanceId);
        Map<String, Object> result = new HashMap<>();
        result.put("ordonnance", ord); result.put("medicaments", meds);
        return result;
    }

    public Page<PrescriptionDto> listAll(Pageable pageable) {
        return ordRepo.findAll(pageable).map(this::toDto);
    }

    public Page<PrescriptionDto> getByPatientPage(Long patientId, Pageable pageable) {
        return ordRepo.findByPatientIdOrderByDatePrescriptionDesc(patientId, pageable).map(this::toDto);
    }

    @Transactional
    public void delete(Long id) {
        ordRepo.deleteById(id);
    }

    public PrescriptionDto toDto(Ordonnance ord) {
        return PrescriptionDto.builder()
                .id(ord.getId())
                .patientId(ord.getPatientId())
                .medecinId(ord.getMedecinId())
                .consultationId(ord.getConsultationId())
                .datePrescription(ord.getDatePrescription())
                .diagnostic(ord.getDiagnostic())
                .instructions(ord.getInstructions())
                .statut(ord.getStatut() != null ? ord.getStatut().name() : null)
                .build();
    }
}