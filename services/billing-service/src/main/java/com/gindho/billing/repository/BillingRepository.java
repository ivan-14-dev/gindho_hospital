package com.gindho.billing.repository;

import com.gindho.billing.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository extends JpaRepository<Facture, Long> {
}
