package com.gindho.bed.repository;
import com.gindho.bed.model.Lit; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LitRepository extends JpaRepository<Lit, Long> {
    List<Lit> findByChambreId(Long chambreId);
    List<Lit> findByOccupeFalse();
    long countByChambreId(Long chambreId);
    long countByChambreIdAndOccupeTrue(Long chambreId);
}