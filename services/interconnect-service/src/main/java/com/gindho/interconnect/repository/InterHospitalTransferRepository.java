package com.gindho.interconnect.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gindho.interconnect.model.InterHospitalTransfer;
import com.gindho.interconnect.model.InterHospitalTransfer.TransferStatus;

@Repository
public interface InterHospitalTransferRepository extends JpaRepository<InterHospitalTransfer, Long> {

    Optional<InterHospitalTransfer> findByTransferRef(String transferRef);

    List<InterHospitalTransfer> findBySourceHospitalId(String sourceHospitalId);

    List<InterHospitalTransfer> findByTargetHospitalId(String targetHospitalId);

    List<InterHospitalTransfer> findByPatientId(String patientId);

    List<InterHospitalTransfer> findByStatus(TransferStatus status);

    List<InterHospitalTransfer> findBySourceHospitalIdAndStatus(String sourceHospitalId, TransferStatus status);

    List<InterHospitalTransfer> findByTargetHospitalIdAndStatus(String targetHospitalId, TransferStatus status);

    long countByStatus(TransferStatus status);

    long countBySourceHospitalId(String sourceHospitalId);

    long countByTargetHospitalId(String targetHospitalId);
}