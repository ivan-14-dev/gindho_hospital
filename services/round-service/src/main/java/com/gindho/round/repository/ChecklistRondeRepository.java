package com.gindho.round.repository;
import com.gindho.round.model.ChecklistRonde;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ChecklistRondeRepository extends JpaRepository<ChecklistRonde, Long> {
    List<ChecklistRonde> findByRondeId(Long rondeId);
}