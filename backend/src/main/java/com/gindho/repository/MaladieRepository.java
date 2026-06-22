package com.gindho.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.model.Maladie;

@Repository
public interface MaladieRepository extends JpaRepository<Maladie, Long> {

    Optional<Maladie> findByNom(String nom);

    List<Maladie> findByActifTrue();
}
