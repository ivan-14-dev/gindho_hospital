package com.gindho.repository;

import com.gindho.model.StockConsommable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockConsommableRepository extends JpaRepository<StockConsommable, Long> {
    List<StockConsommable> findByCategorie(String categorie);
    List<StockConsommable> findByQuantiteLessThan(int seuil);
    List<StockConsommable> findByDatePeremptionBefore(LocalDate date);
    List<StockConsommable> findByNomContainingIgnoreCase(String nom);
}