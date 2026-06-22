package com.gindho.repository;

import com.gindho.model.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findByMedecinIdAndActifTrue(Long medecinId);
    List<Disponibilite> findByMedecinId(Long medecinId);
}
