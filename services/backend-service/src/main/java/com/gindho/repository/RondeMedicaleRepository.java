package com.gindho.repository;
import com.gindho.model.RondeMedicale;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface RondeMedicaleRepository extends JpaRepository<RondeMedicale, Long> {
    List<RondeMedicale> findByMedecinIdOrderByDateDebutDesc(Long medecinId);
    List<RondeMedicale> findByDateDebutBetweenOrderByDateDebutAsc(LocalDateTime debut, LocalDateTime fin);
}