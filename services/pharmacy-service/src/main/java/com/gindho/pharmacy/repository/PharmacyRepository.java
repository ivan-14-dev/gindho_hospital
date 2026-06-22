package com.gindho.pharmacy.repository;

import com.gindho.pharmacy.model.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PharmacyRepository extends JpaRepository<Medicament, Long> {
}
