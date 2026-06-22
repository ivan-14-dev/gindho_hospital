package com.gindho.repository;
import com.gindho.model.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; import java.util.List;
public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    List<Equipement> findByStatut(String statut);
    List<Equipement> findByDateProchaineMaintenanceBefore(LocalDate date);
}