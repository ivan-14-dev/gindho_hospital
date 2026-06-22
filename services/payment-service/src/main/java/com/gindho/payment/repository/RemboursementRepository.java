package com.gindho.payment.repository;
import com.gindho.payment.model.Remboursement; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
    List<Remboursement> findByTransactionId(Long transactionId);
}