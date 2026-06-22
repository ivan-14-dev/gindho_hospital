package com.gindho.reporting.repository;
import com.gindho.reporting.model.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
public interface RapportRepository extends JpaRepository<Rapport, Long> {}