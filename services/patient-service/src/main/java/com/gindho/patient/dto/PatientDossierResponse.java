package com.gindho.patient.dto;

import lombok.*;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDossierResponse {
    private PatientDto patient;
    private String resume;
    @Builder.Default
    private List<ContactDto> contacts = new ArrayList<>();
    @Builder.Default
    private List<AssurancePatientDto> assurances = new ArrayList<>();
    @Builder.Default
    private List<DocumentDto> documents = new ArrayList<>();
    @Builder.Default
    private List<IdentifiantDto> identifiants = new ArrayList<>();
}
