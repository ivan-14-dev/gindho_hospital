package com.gindho.interconnect.dto;

import com.gindho.interconnect.model.InterHospitalTransfer.TransferType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TransferRequest {

    @NotBlank(message = "Patient ID is required")
    private String patientId;

    private String patientName;

    private String patientNationalId;

    @NotBlank(message = "Target hospital ID is required")
    private String targetHospitalId;

    @NotNull(message = "Transfer type is required")
    private TransferType transferType;

    private String reason;

    private String medicalSummary;

    private String encryptedPatientData;

    private String attachmentsMetadata;

    private boolean consentObtained;

    private String consentDocumentRef;
}