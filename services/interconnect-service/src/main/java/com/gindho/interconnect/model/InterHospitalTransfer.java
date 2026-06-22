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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inter_hospital_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterHospitalTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transferRef;

    @Column(nullable = false)
    private String patientId;

    private String patientName;

    private String patientNationalId;

    @Column(nullable = false)
    private String sourceHospitalId;

    @Column(nullable = false)
    private String sourceHospitalName;

    @Column(nullable = false)
    private String targetHospitalId;

    @Column(nullable = false)
    private String targetHospitalName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferType transferType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String reason;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String medicalSummary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String encryptedPatientData;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String attachmentsMetadata;

    @Column(nullable = false)
    private String initiatedBy;

    private String approvedBy;

    private String receivedBy;

    @Column(nullable = false)
    private LocalDateTime initiatedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime completedAt;

    private LocalDateTime rejectedAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(nullable = false)
    private boolean consentObtained;

    @Column(columnDefinition = "TEXT")
    private String consentDocumentRef;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private boolean acknowledged;

    private LocalDateTime acknowledgedAt;

    public enum TransferType {
        PATIENT_TRANSFER,
        MEDICAL_RECORD_SHARING,
        LAB_RESULT_SHARING,
        IMAGING_SHARING,
        CONSULTATION_REQUEST,
        EMERGENCY_TRANSFER
    }

    public enum TransferStatus {
        PENDING,
        AWAITING_APPROVAL,
        APPROVED,
        IN_TRANSIT,
        COMPLETED,
        REJECTED,
        CANCELLED,
        FAILED
    }

    @PrePersist
    protected void onCreate() {
        this.initiatedAt = LocalDateTime.now();
        this.retryCount = 0;
        this.acknowledged = false;
        if (this.status == null) {
            this.status = TransferStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.status == TransferStatus.COMPLETED && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
}