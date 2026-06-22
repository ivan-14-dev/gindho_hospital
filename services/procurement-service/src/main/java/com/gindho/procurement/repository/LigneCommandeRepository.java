package com.gindho.procurement.repository;
import com.gindho.procurement.model.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
    List<LigneCommande> findByBonCommandeId(Long bonCommandeId);
}