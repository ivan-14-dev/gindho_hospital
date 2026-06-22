package com.gindho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.model.Lit;

@Repository
public interface LitRepository extends JpaRepository<Lit, Long> {
    Optional<Lit> findByNumeroLit(String numeroLit);

    List<Lit> findByChambreIdAndActifTrue(Long chambreId);

    List<Lit> findByChambreIdAndStatut(Long chambreId, Lit.StatutLit statut);

    List<Lit> findByChambreIdAndStatutNot(Long chambreId, Lit.StatutLit statut);
}
