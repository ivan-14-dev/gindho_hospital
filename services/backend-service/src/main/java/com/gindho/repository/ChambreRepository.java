package com.gindho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.model.Chambre;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre, Long> {
    Optional<Chambre> findByNumeroChambre(String numeroChambre);
    List<Chambre> findByActifTrue();
}
