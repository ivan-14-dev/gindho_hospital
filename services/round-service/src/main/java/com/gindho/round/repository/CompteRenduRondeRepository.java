package com.gindho.round.repository;
import com.gindho.round.model.CompteRenduRonde;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CompteRenduRondeRepository extends JpaRepository<CompteRenduRonde, Long> {
    Optional<CompteRenduRonde> findByRondeId(Long rondeId);
}