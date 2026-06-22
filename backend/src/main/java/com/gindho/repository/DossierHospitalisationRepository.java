package com.gindho.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.model.DossierHospitalisation;
import com.gindho.model.Hospitalisation;

@Repository
public interface DossierHospitalisationRepository extends JpaRepository<DossierHospitalisation, Long> {

    Optional<DossierHospitalisation> findByHospitalisationId(Long hospitalisationId);

    // utile si on décide de partir de l’entity Hospitalisation
    Optional<DossierHospitalisation> findByHospitalisation(Hospitalisation hospitalisation);
}
