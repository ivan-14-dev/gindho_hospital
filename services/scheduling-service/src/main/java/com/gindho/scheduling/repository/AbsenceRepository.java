package com.gindho.scheduling.repository;
import com.gindho.scheduling.model.Absence; import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; import java.util.List;
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByEmployeIdAndDateDebutBetween(Long eid, LocalDate start, LocalDate end);
}