package com.gindho.pharmacy.repository;
import com.gindho.pharmacy.model.Medicament; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    Optional<Medicament> findByCode(String code);
    java.util.List<Medicament> findByNomContainingIgnoreCase(String medicament);
}