package com.gindho.repository;
import com.gindho.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByResoluFalseOrderByDateDeclarationDesc();
    List<Incident> findByTypeIncident(String type);
}