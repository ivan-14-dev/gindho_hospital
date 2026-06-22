package com.gindho.interconnect.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.interconnect.dto.InterHospitalTransferDto;
import com.gindho.interconnect.dto.TransferRequest;
import com.gindho.interconnect.model.HospitalPartner;
import com.gindho.interconnect.model.InterHospitalTransfer;
import com.gindho.interconnect.model.InterHospitalTransfer.TransferStatus;
import com.gindho.interconnect.repository.HospitalPartnerRepository;
import com.gindho.interconnect.repository.InterHospitalTransferRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InterHospitalTransferService {

    private final InterHospitalTransferRepository transferRepository;
    private final HospitalPartnerRepository partnerRepository;
    private final InterconnectRemoteClient remoteClient;

    public InterHospitalTransferDto initiateTransfer(TransferRequest request, String sourceHospitalId) {
        HospitalPartner source = partnerRepository.findByHospitalId(sourceHospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Source hospital not found: " + sourceHospitalId));

        HospitalPartner target = partnerRepository.findByHospitalId(request.getTargetHospitalId())
                .orElseThrow(() -> new EntityNotFoundException("Target hospital not found: " + request.getTargetHospitalId()));

        if (target.getStatus() != HospitalPartner.PartnerStatus.ACTIVE) {
            throw new IllegalStateException("Target hospital is not active: " + target.getName());
        }

        String transferRef = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        InterHospitalTransfer transfer = InterHospitalTransfer.builder()
                .transferRef(transferRef)
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .patientNationalId(request.getPatientNationalId())
                .sourceHospitalId(source.getHospitalId())
                .sourceHospitalName(source.getName())
                .targetHospitalId(target.getHospitalId())
                .targetHospitalName(target.getName())
                .transferType(request.getTransferType())
                .status(TransferStatus.PENDING)
                .reason(request.getReason())
                .medicalSummary(request.getMedicalSummary())
                .encryptedPatientData(request.getEncryptedPatientData())
                .attachmentsMetadata(request.getAttachmentsMetadata())
                .initiatedBy(sourceHospitalId)
                .consentObtained(request.isConsentObtained())
                .consentDocumentRef(request.getConsentDocumentRef())
                .build();

        InterHospitalTransfer saved = transferRepository.save(transfer);

        // Notify target hospital via remote call
        try {
            boolean notified = remoteClient.notifyTransfer(target, saved);
            if (notified) {
                saved.setStatus(TransferStatus.AWAITING_APPROVAL);
                saved = transferRepository.save(saved);
                log.info("Transfer {} notified to {}", transferRef, target.getName());
            }
        } catch (Exception e) {
            log.warn("Could not notify target hospital {} for transfer {}: {}", target.getName(), transferRef, e.getMessage());
        }

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<InterHospitalTransferDto> getIncomingTransfers(String hospitalId) {
        return transferRepository.findByTargetHospitalId(hospitalId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterHospitalTransferDto> getOutgoingTransfers(String hospitalId) {
        return transferRepository.findBySourceHospitalId(hospitalId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterHospitalTransferDto> getPatientTransfers(String patientId) {
        return transferRepository.findByPatientId(patientId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public InterHospitalTransferDto approveTransfer(Long transferId, String hospitalId) {
        InterHospitalTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found: " + transferId));

        if (!transfer.getTargetHospitalId().equals(hospitalId)) {
            throw new IllegalStateException("Only the target hospital can approve this transfer");
        }

        transfer.setStatus(TransferStatus.APPROVED);
        transfer.setApprovedBy(hospitalId);
        transfer.setApprovedAt(LocalDateTime.now());
        InterHospitalTransfer saved = transferRepository.save(transfer);
        log.info("Transfer {} approved by {}", saved.getTransferRef(), hospitalId);
        return toDto(saved);
    }

    public InterHospitalTransferDto completeTransfer(Long transferId, String hospitalId) {
        InterHospitalTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found: " + transferId));

        if (!transfer.getTargetHospitalId().equals(hospitalId)) {
            throw new IllegalStateException("Only the target hospital can complete this transfer");
        }

        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setReceivedBy(hospitalId);
        transfer.setCompletedAt(LocalDateTime.now());
        InterHospitalTransfer saved = transferRepository.save(transfer);
        log.info("Transfer {} completed by {}", saved.getTransferRef(), hospitalId);
        return toDto(saved);
    }

    public InterHospitalTransferDto rejectTransfer(Long transferId, String hospitalId, String reason) {
        InterHospitalTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found: " + transferId));

        if (!transfer.getTargetHospitalId().equals(hospitalId)) {
            throw new IllegalStateException("Only the target hospital can reject this transfer");
        }

        transfer.setStatus(TransferStatus.REJECTED);
        transfer.setRejectedAt(LocalDateTime.now());
        transfer.setRejectionReason(reason);
        InterHospitalTransfer saved = transferRepository.save(transfer);
        log.info("Transfer {} rejected by {}: {}", saved.getTransferRef(), hospitalId, reason);
        return toDto(saved);
    }

    public InterHospitalTransferDto acknowledgeTransfer(Long transferId, String hospitalId) {
        InterHospitalTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found: " + transferId));

        transfer.setAcknowledged(true);
        transfer.setAcknowledgedAt(LocalDateTime.now());
        InterHospitalTransfer saved = transferRepository.save(transfer);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public InterHospitalTransferDto getTransferByRef(String transferRef) {
        InterHospitalTransfer transfer = transferRepository.findByTransferRef(transferRef)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found: " + transferRef));
        return toDto(transfer);
    }

    @Transactional(readOnly = true)
    public long getPendingCount(String hospitalId) {
        return transferRepository.countByTargetHospitalId(hospitalId);
    }

    private InterHospitalTransferDto toDto(InterHospitalTransfer t) {
        return InterHospitalTransferDto.builder()
                .id(t.getId())
                .transferRef(t.getTransferRef())
                .patientId(t.getPatientId())
                .patientName(t.getPatientName())
                .patientNationalId(t.getPatientNationalId())
                .sourceHospitalId(t.getSourceHospitalId())
                .sourceHospitalName(t.getSourceHospitalName())
                .targetHospitalId(t.getTargetHospitalId())
                .targetHospitalName(t.getTargetHospitalName())
                .transferType(t.getTransferType())
                .status(t.getStatus())
                .reason(t.getReason())
                .medicalSummary(t.getMedicalSummary())
                .attachmentsMetadata(t.getAttachmentsMetadata())
                .initiatedBy(t.getInitiatedBy())
                .approvedBy(t.getApprovedBy())
                .receivedBy(t.getReceivedBy())
                .consentObtained(t.isConsentObtained())
                .consentDocumentRef(t.getConsentDocumentRef())
                .build();
    }
}