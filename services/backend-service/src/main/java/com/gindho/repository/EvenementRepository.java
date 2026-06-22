package com.gindho.repository;
import com.gindho.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    List<Evenement> findByTypeEvenement(String type);
    List<Evenement> findByDateDebutBetweenOrderByDateDebutAsc(LocalDateTime debut, LocalDateTime fin);
}