package com.gindho.procurement.repository;
import com.gindho.procurement.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
}