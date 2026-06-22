package com.gindho.repository;

import com.gindho.model.AdministrationMedicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdministrationMedicamentRepository extends JpaRepository<AdministrationMedicament, Long> {
    List<AdministrationMedicament> findByPatientIdOrderByDateAdministrationDesc(Long patientId);
    List<AdministrationMedicament> findByHospitalisationIdOrderByDateAdministrationDesc(Long hospitalisationId);
    List<AdministrationMedicament> findByPatientIdAndAdministreFalse(Long patientId);
}