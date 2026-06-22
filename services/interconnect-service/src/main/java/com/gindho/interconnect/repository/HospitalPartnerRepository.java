package com.gindho.interconnect.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.interconnect.model.HospitalPartner;
import com.gindho.interconnect.model.HospitalPartner.PartnerStatus;
import com.gindho.interconnect.model.HospitalPartner.TrustLevel;

@Repository
public interface HospitalPartnerRepository extends JpaRepository<HospitalPartner, Long> {

    Optional<HospitalPartner> findByHospitalId(String hospitalId);

    Optional<HospitalPartner> findByApiKey(String apiKey);

    List<HospitalPartner> findByStatus(PartnerStatus status);

    List<HospitalPartner> findByTrustLevel(TrustLevel trustLevel);

    List<HospitalPartner> findByStatusAndTrustLevel(PartnerStatus status, TrustLevel trustLevel);

    boolean existsByHospitalId(String hospitalId);

    long countByStatus(PartnerStatus status);
}