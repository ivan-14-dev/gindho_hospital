package com.gindho.outgoing.repository;

import com.gindho.outgoing.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    List<Evenement> findByPubliqueTrueAndDateDebutAfterOrderByDateDebutAsc(LocalDateTime now);
    List<Evenement> findByTypeAndStatutOrderByDateDebutAsc(Evenement.TypeEvenement type, Evenement.StatutEvenement statut);
    List<Evenement> findByStatutOrderByDateDebutAsc(Evenement.StatutEvenement statut);
}