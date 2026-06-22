package com.gindho.interconnect.dto;

import com.gindho.interconnect.model.HospitalPartner.TrustLevel;
import com.gindho.interconnect.model.HospitalPartner.PartnerStatus;

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
public class HospitalPartnerDto {

    private Long id;
    private String hospitalId;
    private String name;
    private String baseUrl;
    private TrustLevel trustLevel;
    private PartnerStatus status;
    private String contactEmail;
    private String contactPhone;
    private String country;
    private String city;
    private String description;
    private boolean mtlsEnabled;
    private String allowedIpRanges;
    private boolean online;
}