package com.gindho.billing.repository;

import com.gindho.billing.model.Facture;
import com.gindho.billing.model.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List; import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FactureRepository extends JpaRepository<Facture, Long> {
    Optional<Facture> findByNumeroFacture(String numeroFacture);
    List<Facture> findByPatientIdOrderByDateEmissionDesc(Long patientId);
    List<Facture> findByStatut(StatutFacture statut);
    Page<Facture> findByStatut(StatutFacture statut, Pageable pageable);
    Page<Facture> findByPatientIdOrderByDateEmissionDesc(Long patientId, Pageable pageable);
    @Query("SELECT COALESCE(SUM(f.montant), 0) FROM Facture f WHERE f.statut = 'PAYEE'")
    BigDecimal totalPaye();
}