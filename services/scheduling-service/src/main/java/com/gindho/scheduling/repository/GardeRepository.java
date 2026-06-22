package com.gindho.scheduling.repository;
import com.gindho.scheduling.model.Garde; import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime; import java.util.List;
public interface GardeRepository extends JpaRepository<Garde, Long> {
    List<Garde> findByEmployeIdAndDateDebutBetween(Long eid, LocalDateTime start, LocalDateTime end);
    List<Garde> findByDateDebutBetween(LocalDateTime start, LocalDateTime end);
    List<Garde> findByServiceAndDateDebutBetween(String service, LocalDateTime start, LocalDateTime end);
}