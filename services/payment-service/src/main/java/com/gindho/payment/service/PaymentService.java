package com.gindho.payment.service;
import com.gindho.payment.model.*; import com.gindho.payment.repository.*;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
@Service @RequiredArgsConstructor @Transactional @Slf4j
public class PaymentService {
    private final PaiementTransactionRepository txRepo;
    private final RemboursementRepository rembRepo;
    private long seq = 1000L;
    public PaiementTransaction processPayment(PaiementTransaction tx) {
        tx.setCodeTransaction("PAY-" + java.time.Year.now().getValue() + "-" + (++seq));
        tx.setDatePaiement(LocalDateTime.now());
        tx.setStatut(StatutTransaction.REUSSI);
        log.info("Payment processed: {} for invoice {}", tx.getCodeTransaction(), tx.getFactureId());
        return txRepo.save(tx);
    }
    public Remboursement refund(Long transactionId, BigDecimal montant, String motif) {
        Remboursement r = new Remboursement();
        r.setTransactionId(transactionId); r.setMontant(montant); r.setMotif(motif);
        r.setDateRemboursement(LocalDateTime.now()); r.setStatut(StatutRemboursement.EN_ATTENTE);
        log.info("Refund requested for transaction {}: {}", transactionId, montant);
        return rembRepo.save(r);
    }
    @Transactional(readOnly=true) public List<PaiementTransaction> getByInvoice(Long factureId) { return txRepo.findByFactureId(factureId); }
    @Transactional(readOnly=true) public List<PaiementTransaction> getByPatient(Long patientId) { return txRepo.findByPatientId(patientId); }
    @Transactional(readOnly=true) public List<PaiementTransaction> getByStatut(StatutTransaction statut) { return txRepo.findByStatut(statut); }
}