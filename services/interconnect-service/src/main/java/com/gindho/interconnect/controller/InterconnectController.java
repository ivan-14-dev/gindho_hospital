package com.gindho.interconnect.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gindho.base.ApiResponse;
import com.gindho.interconnect.dto.HospitalPartnerDto;
import com.gindho.interconnect.dto.InterHospitalTransferDto;
import com.gindho.interconnect.dto.RegisterPartnerRequest;
import com.gindho.interconnect.dto.TransferRequest;
import com.gindho.interconnect.model.HospitalPartner;
import com.gindho.interconnect.model.InterHospitalTransfer;
import com.gindho.interconnect.service.HospitalPartnerService;
import com.gindho.interconnect.service.InterHospitalTransferService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/interconnect")
@RequiredArgsConstructor
@Slf4j
public class InterconnectController {

    private final HospitalPartnerService partnerService;
    private final InterHospitalTransferService transferService;

    // ==================== PUBLIC ENDPOINTS (no auth required) ====================

    @PostMapping("/public/register")
    public ResponseEntity<ApiResponse<HospitalPartnerDto>> registerPartner(
            @Valid @RequestBody RegisterPartnerRequest request) {
        log.info("New partner registration request: {} ({})", request.getName(), request.getHospitalId());
        HospitalPartnerDto result = partnerService.registerPartner(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Partner registered successfully. Awaiting approval.", result));
    }

    @PostMapping("/public/incoming-transfer")
    public ResponseEntity<ApiResponse<String>> receiveTransferNotification(@RequestBody TransferRequest request) {
        log.info("Incoming transfer notification received for patient {} to hospital {}",
                request.getPatientId(), request.getTargetHospitalId());
        return ResponseEntity.ok(ApiResponse.ok("Transfer notification received", "ACK"));
    }

    // ==================== PARTNER MANAGEMENT ====================

    @GetMapping("/partners")
    public ResponseEntity<ApiResponse<List<HospitalPartnerDto>>> listPartners(Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        List<HospitalPartnerDto> partners = partnerService.listAllPartners();
        log.debug("Hospital {} listed {} partners", hospitalId, partners.size());
        return ResponseEntity.ok(ApiResponse.ok("Partners retrieved", partners));
    }

    @GetMapping("/partners/active")
    public ResponseEntity<ApiResponse<List<HospitalPartnerDto>>> listActivePartners(Authentication auth) {
        List<HospitalPartnerDto> partners = partnerService.getActivePartners();
        return ResponseEntity.ok(ApiResponse.ok("Active partners retrieved", partners));
    }

    @GetMapping("/partners/{id}")
    public ResponseEntity<ApiResponse<HospitalPartnerDto>> getPartner(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Partner retrieved", partnerService.getPartner(id)));
    }

    @GetMapping("/partners/by-hospital/{hospitalId}")
    public ResponseEntity<ApiResponse<HospitalPartnerDto>> getPartnerByHospitalId(
            @PathVariable String hospitalId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Partner retrieved", partnerService.getPartnerByHospitalId(hospitalId)));
    }

    @PutMapping("/partners/{id}/activate")
    public ResponseEntity<ApiResponse<HospitalPartnerDto>> activatePartner(
            @PathVariable Long id, Authentication auth) {
        log.info("Partner activation requested for ID {} by {}", id, resolveHospitalId(auth));
        return ResponseEntity.ok(
                ApiResponse.ok("Partner activated", partnerService.activatePartner(id)));
    }

    @PutMapping("/partners/{id}/suspend")
    public ResponseEntity<ApiResponse<HospitalPartnerDto>> suspendPartner(
            @PathVariable Long id,
            @RequestParam(defaultValue = "No reason provided") String reason,
            Authentication auth) {
        log.info("Partner suspension requested for ID {} by {}: {}", id, resolveHospitalId(auth), reason);
        return ResponseEntity.ok(
                ApiResponse.ok("Partner suspended", partnerService.suspendPartner(id, reason)));
    }

    // ==================== HEARTBEAT ====================

    @PostMapping("/heartbeat")
    public ResponseEntity<ApiResponse<String>> heartbeat(Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        partnerService.processHeartbeat(hospitalId);
        return ResponseEntity.ok(ApiResponse.ok("Heartbeat received", "OK"));
    }

    @GetMapping("/health/{hospitalId}")
    public ResponseEntity<ApiResponse<Boolean>> checkHealth(@PathVariable String hospitalId) {
        boolean online = partnerService.isOnline(hospitalId);
        return ResponseEntity.ok(ApiResponse.ok("Health status", online));
    }

    // ==================== TRANSFERS ====================

    @PostMapping("/transfers")
    public ResponseEntity<ApiResponse<InterHospitalTransferDto>> initiateTransfer(
            @Valid @RequestBody TransferRequest request,
            Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        log.info("Transfer initiated by {} to {} for patient {}",
                hospitalId, request.getTargetHospitalId(), request.getPatientId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Transfer initiated", transferService.initiateTransfer(request, hospitalId)));
    }

    @GetMapping("/transfers/incoming")
    public ResponseEntity<ApiResponse<List<InterHospitalTransferDto>>> getIncomingTransfers(Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        List<InterHospitalTransferDto> transfers = transferService.getIncomingTransfers(hospitalId);
        return ResponseEntity.ok(ApiResponse.ok("Incoming transfers retrieved", transfers));
    }

    @GetMapping("/transfers/outgoing")
    public ResponseEntity<ApiResponse<List<InterHospitalTransferDto>>> getOutgoingTransfers(Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        List<InterHospitalTransferDto> transfers = transferService.getOutgoingTransfers(hospitalId);
        return ResponseEntity.ok(ApiResponse.ok("Outgoing transfers retrieved", transfers));
    }

    @GetMapping("/transfers/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<InterHospitalTransferDto>>> getPatientTransfers(
            @PathVariable String patientId) {
        return ResponseEntity.ok(
                ApiResponse.ok("Patient transfers retrieved", transferService.getPatientTransfers(patientId)));
    }

    @GetMapping("/transfers/{transferRef}")
    public ResponseEntity<ApiResponse<InterHospitalTransferDto>> getTransferByRef(
            @PathVariable String transferRef) {
        return ResponseEntity.ok(
                ApiResponse.ok("Transfer retrieved", transferService.getTransferByRef(transferRef)));
    }

    @PutMapping("/transfers/{id}/approve")
    public ResponseEntity<ApiResponse<InterHospitalTransferDto>> approveTransfer(
            @PathVariable Long id, Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        return ResponseEntity.ok(
                ApiResponse.ok("Transfer approved", transferService.approveTransfer(id, hospitalId)));
    }

    @PutMapping("/transfers/{id}/complete")
    public ResponseEntity<ApiResponse<InterHospitalTransferDto>> completeTransfer(
            @PathVariable Long id, Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        return ResponseEntity.ok(
                ApiResponse.ok("Transfer completed", transferService.completeTransfer(id, hospitalId)));
    }

    @PutMapping("/transfers/{id}/reject")
    public ResponseEntity<ApiResponse<InterHospitalTransferDto>> rejectTransfer(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        return ResponseEntity.ok(
                ApiResponse.ok("Transfer rejected", transferService.rejectTransfer(id, hospitalId, reason)));
    }

    @PutMapping("/transfers/{id}/acknowledge")
    public ResponseEntity<ApiResponse<InterHospitalTransferDto>> acknowledgeTransfer(
            @PathVariable Long id, Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        return ResponseEntity.ok(
                ApiResponse.ok("Transfer acknowledged", transferService.acknowledgeTransfer(id, hospitalId)));
    }

    @GetMapping("/transfers/pending-count")
    public ResponseEntity<ApiResponse<Long>> getPendingCount(Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        long count = transferService.getPendingCount(hospitalId);
        return ResponseEntity.ok(ApiResponse.ok("Pending count", count));
    }

    // ==================== DATA SHARING ====================

    @PostMapping("/data-request")
    public ResponseEntity<ApiResponse<String>> requestPatientData(
            @RequestBody TransferRequest request, Authentication auth) {
        String hospitalId = resolveHospitalId(auth);
        log.info("Data request from {} for patient {}", hospitalId, request.getPatientId());
        return ResponseEntity.ok(
                ApiResponse.ok("Data request sent", transferService.initiateTransfer(request, hospitalId).getTransferRef()));
    }

    // ==================== HELPERS ====================

    private String resolveHospitalId(Authentication auth) {
        if (auth != null && auth.getPrincipal() instanceof HospitalPartner partner) {
            return partner.getHospitalId();
        }
        if (auth != null && auth.getPrincipal() instanceof InterHospitalTransfer transfer) {
            return transfer.getSourceHospitalId();
        }
        return "unknown";
    }
}