package com.gindho.repository;

import com.gindho.model.Paiement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    Optional<Paiement> findByTransactionRef(String transactionRef);

    List<Paiement> findByStatut(Paiement.StatutPaiement statut);

    @Query("select p from Paiement p where p.facture.id = :factureId")
    List<Paiement> findByFactureId(@Param("factureId") Long factureId);

    @Query("select p from Paiement p where p.facture.patient.id = :patientId")
    List<Paiement> findByPatientId(@Param("patientId") Long patientId);

    @Query("select p from Paiement p where p.facture.medecin.id = :medecinId")
    List<Paiement> findByMedecinId(@Param("medecinId") Long medecinId);
}
