package com.gindho.payment.repository;
import com.gindho.payment.model.PaiementTransaction;
import com.gindho.payment.model.StatutTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional;
public interface PaiementTransactionRepository extends JpaRepository<PaiementTransaction, Long> {
    List<PaiementTransaction> findByFactureId(Long factureId);
    List<PaiementTransaction> findByPatientId(Long patientId);
    Optional<PaiementTransaction> findByCodeTransaction(String code);
    List<PaiementTransaction> findByStatut(StatutTransaction statut);
}