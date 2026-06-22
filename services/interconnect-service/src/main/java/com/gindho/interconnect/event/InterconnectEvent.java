package com.gindho.interconnect.event;

import com.gindho.kafka.BaseEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InterconnectEvent extends BaseEvent {
    public static final String TRANSFER_INITIATED = "InterHospitalTransferInitiated";
    public static final String TRANSFER_COMPLETED = "InterHospitalTransferCompleted";
    public static final String TRANSFER_REJECTED = "InterHospitalTransferRejected";
    public static final String DATA_SHARED = "InterHospitalDataShared";
    public static final String PARTNER_ONBOARDED = "InterHospitalPartnerOnboarded";
    public static final String PARTNER_STATUS_CHANGED = "InterHospitalPartnerStatusChanged";
    public static final String HEARTBEAT = "InterHospitalHeartbeat";
}