package com.gindho.pharmacy.repository;

import com.gindho.pharmacy.model.Maladie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaladieRepository extends JpaRepository<Maladie, Long> {
}
