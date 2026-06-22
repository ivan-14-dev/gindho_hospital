package com.gindho.interconnect.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hospital_partners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String hospitalId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String baseUrl;

    @Column(nullable = false, unique = true)
    private String apiKey;

    @Column(columnDefinition = "TEXT")
    private String publicKey;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrustLevel trustLevel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PartnerStatus status;

    @Column(nullable = false)
    private String contactEmail;

    private String contactPhone;

    private String country;

    private String city;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    private LocalDateTime lastHeartbeatAt;

    private int failedHeartbeats;

    @Column(nullable = false)
    private boolean mtlsEnabled;

    @Column(columnDefinition = "TEXT")
    private String allowedIpRanges;

    public enum TrustLevel {
        TRUSTED,
        LIMITED,
        UNTRUSTED
    }

    public enum PartnerStatus {
        ACTIVE,
        SUSPENDED,
        PENDING_APPROVAL,
        DECOMMISSIONED
    }

    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
        this.failedHeartbeats = 0;
        if (this.status == null) {
            this.status = PartnerStatus.PENDING_APPROVAL;
        }
        if (this.trustLevel == null) {
            this.trustLevel = TrustLevel.LIMITED;
        }
    }
}