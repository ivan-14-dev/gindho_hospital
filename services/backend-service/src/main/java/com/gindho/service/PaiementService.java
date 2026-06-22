package com.gindho.service;

import java.math.BigDecimal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.gindho.dto.PaiementDto;
import com.gindho.model.Facture;
import com.gindho.model.Paiement;
import com.gindho.repository.FactureRepository;
import com.gindho.repository.PaiementRepository;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final FactureRepository factureRepository;
    private final PatientAccessService patientAccessService;

    public PaiementService(
            PaiementRepository paiementRepository,
            FactureRepository factureRepository,
            PatientAccessService patientAccessService
    ) {
        this.paiementRepository = paiementRepository;
        this.factureRepository = factureRepository;
        this.patientAccessService = patientAccessService;
    }

    public PaiementDto createPaiement(PaiementDto dto) {
        if (dto == null) throw new IllegalArgumentException("PaiementDto manquant");
        if (dto.getTransactionRef() == null || dto.getTransactionRef().isBlank()) {
            throw new IllegalArgumentException("transactionRef requis (anti double-commit)");
        }
        if (dto.getFactureId() == null) throw new IllegalArgumentException("factureId requis");
        if (dto.getMontant() == null) throw new IllegalArgumentException("montant requis");
        if (dto.getModePaiement() == null || dto.getModePaiement().isBlank()) throw new IllegalArgumentException("modePaiement requis");
        if (dto.getStatut() == null || dto.getStatut().isBlank()) throw new IllegalArgumentException("statut requis");

        // Idempotency: si transactionRef déjà existante => retourner tel quel
        var existing = paiementRepository.findByTransactionRef(dto.getTransactionRef());
        if (existing.isPresent()) {
            return toDto(existing.get());
        }

        Facture facture = factureRepository.findById(dto.getFactureId())
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

        Paiement paiement = Paiement.builder()
                .montant(dto.getMontant())
                .description(dto.getDescription() != null ? dto.getDescription() : "")
                .modePaiement(Paiement.ModePaiement.valueOf(dto.getModePaiement()))
                .statut(Paiement.StatutPaiement.valueOf(dto.getStatut()))
                .transactionRef(dto.getTransactionRef().trim())
                .datePaiement(dto.getDatePaiement() != null ? dto.getDatePaiement() : java.time.LocalDateTime.now())
                .facture(facture)
                .build();

        try {
            Paiement saved = paiementRepository.save(paiement);
            updateFactureAfterPayment(facture, paiement.getMontant());
            return toDto(saved);
        } catch (DataIntegrityViolationException e) {
            // Cas rare: double commit en concurrence => unique constraint transactionRef
            return paiementRepository.findByTransactionRef(dto.getTransactionRef())
                    .map(this::toDto)
                    .orElseThrow(() -> e);
        }
    }

    public java.util.List<PaiementDto> getPaiementsByStatut(Paiement.StatutPaiement statut) {
        return paiementRepository.findByStatut(statut).stream().map(this::toDto).toList();
    }

    public java.util.List<PaiementDto> getPaiementsByPatient(Long patientId, Paiement.StatutPaiement statut) {
        patientAccessService.assertPatientAccess(patientId);

        java.util.List<Paiement> paiements = paiementRepository.findByPatientId(patientId);
        if (statut != null) {
            paiements = paiements.stream()
                    .filter(p -> statut.equals(p.getStatut()))
                    .toList();
        }

        return paiements.stream().map(this::toDto).toList();
    }

    private void updateFactureAfterPayment(Facture facture, BigDecimal paiementMontant) {
        BigDecimal montantTotal = facture.getMontantTotal() != null ? facture.getMontantTotal() : BigDecimal.ZERO;
        BigDecimal montantPayee = facture.getMontantPayee() != null ? facture.getMontantPayee() : BigDecimal.ZERO;

        BigDecimal nouveauMontantPayee = montantPayee.add(paiementMontant != null ? paiementMontant : BigDecimal.ZERO);
        if (nouveauMontantPayee.compareTo(montantTotal) > 0) {
            nouveauMontantPayee = montantTotal;
        }

        facture.setMontantPayee(nouveauMontantPayee);

        if (nouveauMontantPayee.compareTo(montantTotal) >= 0 && montantTotal.compareTo(BigDecimal.ZERO) > 0) {
            facture.setStatut(Facture.StatutFacture.PAYEE);
        } else {
            facture.setStatut(Facture.StatutFacture.EN_ATTENTE);
        }

        factureRepository.save(facture);
    }

    private PaiementDto toDto(Paiement paiement) {
        Facture f = paiement.getFacture();
        return PaiementDto.builder()
                .id(paiement.getId())
                .montant(paiement.getMontant())
                .description(paiement.getDescription())
                .modePaiement(paiement.getModePaiement() != null ? paiement.getModePaiement().name() : null)
                .statut(paiement.getStatut() != null ? paiement.getStatut().name() : null)
                .transactionRef(paiement.getTransactionRef())
                .datePaiement(paiement.getDatePaiement())
                .factureId(f != null ? f.getId() : null)
                .patientId(f != null && f.getPatient() != null ? f.getPatient().getId() : null)
                .medecinId(f != null && f.getMedecin() != null ? f.getMedecin().getId() : null)
                .patientNom(f != null && f.getPatient() != null && f.getPatient().getUser() != null
                        ? f.getPatient().getUser().getPrenom() + " " + f.getPatient().getUser().getNom()
                        : "")
                .medecinNom(f != null && f.getMedecin() != null && f.getMedecin().getUser() != null
                        ? f.getMedecin().getUser().getPrenom() + " " + f.getMedecin().getUser().getNom()
                        : "")
                .build();
    }
}
