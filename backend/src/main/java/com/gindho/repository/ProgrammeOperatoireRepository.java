package com.gindho.repository;
import com.gindho.model.ProgrammeOperatoire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface ProgrammeOperatoireRepository extends JpaRepository<ProgrammeOperatoire, Long> {
    List<ProgrammeOperatoire> findByDateDebutBetweenOrderByDateDebutAsc(LocalDateTime debut, LocalDateTime fin);
    List<ProgrammeOperatoire> findByChirurgienIdOrderByDateDebutDesc(Long chirurgienId);
}