package com.gindho.repository;

import com.gindho.model.Garde;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GardeRepository extends JpaRepository<Garde, Long> {
    List<Garde> findByMedecinIdOrderByDateDebutDesc(Long medecinId);
    List<Garde> findByDateDebutBetweenOrderByDateDebutAsc(LocalDateTime debut, LocalDateTime fin);
    List<Garde> findByMedecinIdAndDateDebutBetween(Long medecinId, LocalDateTime debut, LocalDateTime fin);
}