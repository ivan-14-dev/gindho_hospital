package com.gindho.interconnect.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interconnect_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterconnectAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String sourceHospitalId;

    @Column(nullable = false)
    private String targetHospitalId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean success;

    private String errorMessage;

    @Column(nullable = false)
    private String performedBy;

    @Column(nullable = false)
    private String sourceIp;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private long durationMs;

    public enum ActionType {
        PARTNER_REGISTERED,
        PARTNER_ACTIVATED,
        PARTNER_SUSPENDED,
        PARTNER_DECOMMISSIONED,
        TRANSFER_INITIATED,
        TRANSFER_APPROVED,
        TRANSFER_COMPLETED,
        TRANSFER_REJECTED,
        TRANSFER_FAILED,
        DATA_REQUESTED,
        DATA_SENT,
        DATA_RECEIVED,
        HEARTBEAT_RECEIVED,
        HEARTBEAT_FAILED,
        KEY_EXCHANGED,
        CONSENT_OBTAINED,
        CONSENT_REVOKED,
        ENCRYPTION_ERROR,
        AUTH_FAILED
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}