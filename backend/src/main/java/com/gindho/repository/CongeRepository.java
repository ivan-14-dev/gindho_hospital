package com.gindho.repository;
import com.gindho.model.Conge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CongeRepository extends JpaRepository<Conge, Long> {
    List<Conge> findByPersonnelIdOrderByDateDebutDesc(Long personnelId);
    List<Conge> findByValideFalse();
}