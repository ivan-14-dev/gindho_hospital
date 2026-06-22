package com.gindho.patient.repository;

import com.gindho.patient.model.Medecin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    Medecin findByUserId(Long userId);
    Medecin findByNumeroOrdre(String numeroOrdre);
    Page<Medecin> findBySpecialisationContainingIgnoreCaseOrTelephoneCabinetContainingIgnoreCase(
            String specialisation, String telephoneCabinet, Pageable pageable);
    Page<Medecin> findByDisponible(boolean disponible, Pageable pageable);
}
