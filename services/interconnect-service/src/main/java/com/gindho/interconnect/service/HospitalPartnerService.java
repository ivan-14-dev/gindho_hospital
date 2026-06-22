package com.gindho.interconnect.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gindho.interconnect.dto.HospitalPartnerDto;
import com.gindho.interconnect.dto.RegisterPartnerRequest;
import com.gindho.interconnect.model.HospitalPartner;
import com.gindho.interconnect.model.HospitalPartner.PartnerStatus;
import com.gindho.interconnect.model.HospitalPartner.TrustLevel;
import com.gindho.interconnect.repository.HospitalPartnerRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HospitalPartnerService {

    private final HospitalPartnerRepository hospitalPartnerRepository;

    public HospitalPartnerDto registerPartner(RegisterPartnerRequest request) {
        if (hospitalPartnerRepository.existsByHospitalId(request.getHospitalId())) {
            throw new IllegalArgumentException("Hospital ID already registered: " + request.getHospitalId());
        }

        HospitalPartner partner = HospitalPartner.builder()
                .hospitalId(request.getHospitalId())
                .name(request.getName())
                .baseUrl(request.getBaseUrl())
                .apiKey(UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString())
                .trustLevel(TrustLevel.LIMITED)
                .status(PartnerStatus.PENDING_APPROVAL)
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .country(request.getCountry())
                .city(request.getCity())
                .description(request.getDescription())
                .mtlsEnabled(false)
                .allowedIpRanges(request.getAllowedIpRanges())
                .build();

        HospitalPartner saved = hospitalPartnerRepository.save(partner);
        log.info("New hospital partner registered: {} ({})", saved.getName(), saved.getHospitalId());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<HospitalPartnerDto> listAllPartners() {
        return hospitalPartnerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HospitalPartnerDto getPartner(Long id) {
        HospitalPartner partner = hospitalPartnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + id));
        return toDto(partner);
    }

    @Transactional(readOnly = true)
    public HospitalPartnerDto getPartnerByHospitalId(String hospitalId) {
        HospitalPartner partner = hospitalPartnerRepository.findByHospitalId(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + hospitalId));
        return toDto(partner);
    }

    public HospitalPartnerDto activatePartner(Long id) {
        HospitalPartner partner = hospitalPartnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + id));
        partner.setStatus(PartnerStatus.ACTIVE);
        HospitalPartner saved = hospitalPartnerRepository.save(partner);
        log.info("Hospital partner activated: {} ({})", saved.getName(), saved.getHospitalId());
        return toDto(saved);
    }

    public HospitalPartnerDto suspendPartner(Long id, String reason) {
        HospitalPartner partner = hospitalPartnerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + id));
        partner.setStatus(PartnerStatus.SUSPENDED);
        HospitalPartner saved = hospitalPartnerRepository.save(partner);
        log.warn("Hospital partner suspended: {} ({}) - Reason: {}", saved.getName(), saved.getHospitalId(), reason);
        return toDto(saved);
    }

    public void processHeartbeat(String hospitalId) {
        hospitalPartnerRepository.findByHospitalId(hospitalId).ifPresentOrElse(partner -> {
            partner.setLastHeartbeatAt(LocalDateTime.now());
            partner.setFailedHeartbeats(0);
            hospitalPartnerRepository.save(partner);
        }, () -> log.warn("Heartbeat received for unknown hospital: {}", hospitalId));
    }

    public void recordFailedHeartbeat(String hospitalId) {
        hospitalPartnerRepository.findByHospitalId(hospitalId).ifPresent(partner -> {
            partner.setFailedHeartbeats(partner.getFailedHeartbeats() + 1);
            if (partner.getFailedHeartbeats() >= 5) {
                partner.setStatus(PartnerStatus.SUSPENDED);
                log.warn("Hospital auto-suspended after {} failed heartbeats: {}", partner.getFailedHeartbeats(), hospitalId);
            }
            hospitalPartnerRepository.save(partner);
        });
    }

    @Transactional(readOnly = true)
    public boolean isOnline(String hospitalId) {
        return hospitalPartnerRepository.findByHospitalId(hospitalId)
                .map(p -> p.getLastHeartbeatAt() != null
                        && p.getLastHeartbeatAt().isAfter(LocalDateTime.now().minusMinutes(5))
                        && p.getStatus() == PartnerStatus.ACTIVE)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<HospitalPartnerDto> getActivePartners() {
        return hospitalPartnerRepository.findByStatus(PartnerStatus.ACTIVE).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private HospitalPartnerDto toDto(HospitalPartner p) {
        boolean online = p.getLastHeartbeatAt() != null
                && p.getLastHeartbeatAt().isAfter(LocalDateTime.now().minusMinutes(5));
        return HospitalPartnerDto.builder()
                .id(p.getId())
                .hospitalId(p.getHospitalId())
                .name(p.getName())
                .baseUrl(p.getBaseUrl())
                .trustLevel(p.getTrustLevel())
                .status(p.getStatus())
                .contactEmail(p.getContactEmail())
                .contactPhone(p.getContactPhone())
                .country(p.getCountry())
                .city(p.getCity())
                .description(p.getDescription())
                .mtlsEnabled(p.isMtlsEnabled())
                .allowedIpRanges(p.getAllowedIpRanges())
                .online(online)
                .build();
    }
}