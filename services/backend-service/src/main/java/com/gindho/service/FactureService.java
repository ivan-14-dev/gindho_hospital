package com.gindho.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gindho.dto.FactureDto;
import com.gindho.model.Facture;
import com.gindho.model.Medecin;
import com.gindho.model.Patient;
import com.gindho.repository.FactureRepository;
import com.gindho.repository.MedecinRepository;
import com.gindho.repository.PatientRepository;

@Service
public class FactureService {

    private final FactureRepository factureRepository;
    private final PatientRepository patientRepository;
    private final MedecinRepository medecinRepository;
    private final PatientAccessService patientAccessService;

    public FactureService(
            FactureRepository factureRepository,
            PatientRepository patientRepository,
            MedecinRepository medecinRepository,
            PatientAccessService patientAccessService
    ) {
        this.factureRepository = factureRepository;
        this.patientRepository = patientRepository;
        this.medecinRepository = medecinRepository;
        this.patientAccessService = patientAccessService;
    }

    public List<FactureDto> getFacturesByStatut(Facture.StatutFacture statut) {
        return factureRepository.findByStatut(statut).stream().map(this::toDto).toList();
    }

    public List<FactureDto> getAllFactures() {
        return factureRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<FactureDto> getFacturesByPatient(Long patientId, Facture.StatutFacture statut) {
        patientAccessService.assertPatientAccess(patientId);

        List<Facture> factures = factureRepository.findByPatientId(patientId);
        if (statut != null) {
            factures = factures.stream()
                    .filter(f -> statut.equals(f.getStatut()))
                    .toList();
        }

        return factures.stream().map(this::toDto).toList();
    }

    public FactureDto getFactureById(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
        return toDto(facture);
    }

    public FactureDto createFacture(FactureDto dto) {
        if (dto == null) throw new IllegalArgumentException("FactureDto manquant");
        if (dto.getPatientId() == null) throw new IllegalArgumentException("patientId requis");
        if (dto.getMedecinId() == null) throw new IllegalArgumentException("medecinId requis");
        if (dto.getMontantTotal() == null) throw new IllegalArgumentException("montantTotal requis");
        if (dto.getStatut() == null || dto.getStatut().isBlank()) throw new IllegalArgumentException("statut requis");

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        Medecin medecin = medecinRepository.findById(dto.getMedecinId())
                .orElseThrow(() -> new RuntimeException("Medecin non trouvé"));

        Facture.StatutFacture statut = Facture.StatutFacture.valueOf(dto.getStatut());

        Facture facture = Facture.builder()
                .montantTotal(dto.getMontantTotal())
                .montantPayee(dto.getMontantPayee() != null ? dto.getMontantPayee() : java.math.BigDecimal.ZERO)
                .description(dto.getDescription() != null ? dto.getDescription() : "")
                .statut(statut)
                .dateFacture(dto.getDateFacture() != null ? dto.getDateFacture() : java.time.LocalDateTime.now())
                .echeance(dto.getEcheance())
                .patient(patient)
                .medecin(medecin)
                .build();

        return toDto(factureRepository.save(facture));
    }

    private FactureDto toDto(Facture facture) {
        return FactureDto.builder()
                .id(facture.getId())
                .montantTotal(facture.getMontantTotal())
                .montantPayee(facture.getMontantPayee())
                .description(facture.getDescription())
                .statut(facture.getStatut() != null ? facture.getStatut().name() : null)
                .dateFacture(facture.getDateFacture())
                .echeance(facture.getEcheance())
                .patientId(facture.getPatient() != null ? facture.getPatient().getId() : null)
                .medecinId(facture.getMedecin() != null ? facture.getMedecin().getId() : null)
                .patientNom(facture.getPatient() != null && facture.getPatient().getUser() != null
                        ? facture.getPatient().getUser().getPrenom() + " " + facture.getPatient().getUser().getNom()
                        : "")
                .medecinNom(facture.getMedecin() != null && facture.getMedecin().getUser() != null
                        ? facture.getMedecin().getUser().getPrenom() + " " + facture.getMedecin().getUser().getNom()
                        : "")
                .build();
    }
}
