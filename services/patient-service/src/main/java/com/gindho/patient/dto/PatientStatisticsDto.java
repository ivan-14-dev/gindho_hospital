package com.gindho.patient.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientStatisticsDto {
    private long totalPatients;
    private long activePatients;
    private long archivedPatients;
    private long malePatients;
    private long femalePatients;
    private long otherPatients;
    private long patientsWithContacts;
    private long patientsWithAssurances;
    private long patientsWithDocuments;
    private long patientsCreatedThisMonth;
}
