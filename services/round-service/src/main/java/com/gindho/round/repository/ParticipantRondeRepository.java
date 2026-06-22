package com.gindho.round.repository;
import com.gindho.round.model.ParticipantRonde;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ParticipantRondeRepository extends JpaRepository<ParticipantRonde, Long> {
    List<ParticipantRonde> findByRondeId(Long rondeId);
}