package com.gindho.outgoing.repository;

import com.gindho.outgoing.model.MedecinDisponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.DayOfWeek;
import java.util.List;

public interface MedecinDisponibiliteRepository extends JpaRepository<MedecinDisponibilite, Long> {
    List<MedecinDisponibilite> findByMedecinIdAndActifTrueOrderByJourSemaine(Long medecinId);
    List<MedecinDisponibilite> findByJourSemaineAndActifTrue(DayOfWeek jourSemaine);
}