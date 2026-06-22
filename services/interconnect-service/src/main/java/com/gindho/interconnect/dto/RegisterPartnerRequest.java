package com.gindho.interconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class RegisterPartnerRequest {

    @NotBlank(message = "Hospital ID is required")
    private String hospitalId;

    @NotBlank(message = "Hospital name is required")
    private String name;

    @NotBlank(message = "Base URL is required")
    private String baseUrl;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;

    private String contactPhone;

    private String country;

    private String city;

    private String description;

    private String allowedIpRanges;
}