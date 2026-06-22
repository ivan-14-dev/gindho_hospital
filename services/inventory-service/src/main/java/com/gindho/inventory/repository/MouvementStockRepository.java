package com.gindho.inventory.repository;
import com.gindho.inventory.model.MouvementStock; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    List<MouvementStock> findByProduitIdOrderByDateMouvementDesc(Long produitId);
}