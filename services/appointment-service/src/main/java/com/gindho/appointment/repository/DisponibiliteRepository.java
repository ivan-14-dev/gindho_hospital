package com.gindho.appointment.repository;

import com.gindho.appointment.model.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {

    List<Disponibilite> findByMedecinIdAndDateBetween(Long medecinId, LocalDate start, LocalDate end);

    List<Disponibilite> findByMedecinIdAndDateAndDisponibleTrue(Long medecinId, LocalDate date);

    List<Disponibilite> findByMedecinIdAndDate(Long medecinId, LocalDate date);
}