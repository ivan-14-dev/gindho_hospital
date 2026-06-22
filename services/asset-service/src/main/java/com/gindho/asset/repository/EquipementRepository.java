package com.gindho.asset.repository;

import com.gindho.asset.model.Equipement;
import com.gindho.asset.model.StatutEquipement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    List<Equipement> findByStatut(StatutEquipement statut);
}
