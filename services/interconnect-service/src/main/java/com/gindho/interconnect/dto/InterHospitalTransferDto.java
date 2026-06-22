package com.gindho.interconnect.dto;

import com.gindho.interconnect.model.InterHospitalTransfer.TransferType;
import com.gindho.interconnect.model.InterHospitalTransfer.TransferStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterHospitalTransferDto {

    private Long id;
    private String transferRef;
    private String patientId;
    private String patientName;
    private String patientNationalId;
    private String sourceHospitalId;
    private String sourceHospitalName;
    private String targetHospitalId;
    private String targetHospitalName;
    private TransferType transferType;
    private TransferStatus status;
    private String reason;
    private String medicalSummary;
    private String attachmentsMetadata;
    private String initiatedBy;
    private String approvedBy;
    private String receivedBy;
    private boolean consentObtained;
    private String consentDocumentRef;
}