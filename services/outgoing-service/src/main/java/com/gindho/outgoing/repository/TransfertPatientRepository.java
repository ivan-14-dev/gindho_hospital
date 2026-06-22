package com.gindho.outgoing.repository;

import com.gindho.outgoing.model.TransfertPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransfertPatientRepository extends JpaRepository<TransfertPatient, Long> {
    List<TransfertPatient> findByPatientIdOrderByDateDemandeDesc(Long patientId);
    List<TransfertPatient> findByHopitalDestinationAndStatutOrderByDateDemandeDesc(String hopital, TransfertPatient.StatutTransfert statut);
    List<TransfertPatient> findByStatutOrderByDateDemandeDesc(TransfertPatient.StatutTransfert statut);
    List<TransfertPatient> findByMedecinReferentIdOrderByDateDemandeDesc(Long medecinId);
}