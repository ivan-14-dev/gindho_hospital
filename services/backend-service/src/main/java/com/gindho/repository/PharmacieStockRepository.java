package com.gindho.repository;

import com.gindho.model.PharmacieStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PharmacieStockRepository extends JpaRepository<PharmacieStock, Long> {
    List<PharmacieStock> findByMedicamentContainingIgnoreCase(String medicament);
    List<PharmacieStock> findByDatePeremptionBefore(LocalDate date);
    List<PharmacieStock> findByQuantiteLessThan(int seuil);
    List<PharmacieStock> findByLot(String lot);
}