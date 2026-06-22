package com.gindho.laboratory.repository;

import com.gindho.laboratory.model.Analyse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoryRepository extends JpaRepository<Analyse, Long> {
}
