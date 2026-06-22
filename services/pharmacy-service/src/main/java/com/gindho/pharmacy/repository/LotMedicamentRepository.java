package com.gindho.pharmacy.repository;
import com.gindho.pharmacy.model.LotMedicament; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LotMedicamentRepository extends JpaRepository<LotMedicament, Long> {
    List<LotMedicament> findByMedicamentId(Long medicamentId);
}