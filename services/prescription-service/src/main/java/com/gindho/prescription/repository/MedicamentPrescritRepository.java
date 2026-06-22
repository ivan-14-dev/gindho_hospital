package com.gindho.prescription.repository;
import com.gindho.prescription.model.MedicamentPrescrit; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MedicamentPrescritRepository extends JpaRepository<MedicamentPrescrit, Long> {
    List<MedicamentPrescrit> findByOrdonnanceId(Long ordonnanceId);
}