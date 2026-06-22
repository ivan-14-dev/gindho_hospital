package com.gindho.repository;

import com.gindho.model.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByStatut(Facture.StatutFacture statut);

    List<Facture> findByPatientId(Long patientId);

    List<Facture> findByMedecinId(Long medecinId);
}
