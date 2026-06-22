package com.gindho.billing.repository;

import com.gindho.billing.model.Revenu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate; import java.util.List;

public interface RevenuRepository extends JpaRepository<Revenu, Long> {
    List<Revenu> findByDateBetweenOrderByDateAsc(LocalDate start, LocalDate end);
}