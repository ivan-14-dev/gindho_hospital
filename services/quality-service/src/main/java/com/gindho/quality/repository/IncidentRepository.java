package com.gindho.quality.repository;

import com.gindho.quality.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatutNotOrderByDateDeclarationDesc(String statut);
}