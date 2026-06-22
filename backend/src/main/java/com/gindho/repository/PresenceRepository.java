package com.gindho.repository;
import com.gindho.model.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; import java.util.List;
public interface PresenceRepository extends JpaRepository<Presence, Long> {
    List<Presence> findByPersonnelIdOrderByDateDesc(Long personnelId);
    List<Presence> findByPersonnelIdAndDateBetween(Long personnelId, LocalDate debut, LocalDate fin);
}