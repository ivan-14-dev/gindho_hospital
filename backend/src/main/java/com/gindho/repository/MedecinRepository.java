package com.gindho.repository;

import com.gindho.model.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    Optional<Medecin> findByNumeroOrdre(String numeroOrdre);
    Optional<Medecin> findByUserEmail(String email);
    Medecin findByUserId(Long userId);
    List<Medecin> findByDisponibleTrue();
    
    @Query("SELECT m FROM Medecin m WHERE " +
           "LOWER(m.user.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.user.prenom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.specialisation) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Medecin> findByNomOrPrenomOrSpecialisationContainingIgnoreCase(String query, Pageable pageable);
}
