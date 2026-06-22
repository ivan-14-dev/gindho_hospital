package com.gindho.insurance.repository;

import com.gindho.insurance.model.AssurancePatient;
import com.gindho.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AssurancePatientRepository extends BaseRepository<AssurancePatient> {
    List<AssurancePatient> findByPatientId(Long patientId);
    List<AssurancePatient> findByPatientIdAndActifTrue(Long patientId);
    Optional<AssurancePatient> findByNumeroPolice(String numeroPolice);
    List<AssurancePatient> findByCompagnieContainingIgnoreCase(String compagnie);
    java.util.List<AssurancePatient> findByCompagnieContainingIgnoreCase(String compagnie, org.springframework.data.domain.Pageable pageable);
}
