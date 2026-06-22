package com.gindho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.model.Medicament;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    Optional<Medicament> findByNom(String nom);

    List<Medicament> findByActifTrue();
}
