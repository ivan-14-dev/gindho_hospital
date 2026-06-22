package com.gindho.inventory.repository;
import com.gindho.inventory.model.Produit; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByQuantiteStockLessThanEqual(int seuil);
}