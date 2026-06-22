package com.gindho.emr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdministrationMedicamentDto {
    private Long id;
    private Long patientId;
    private Long hospitalisationId;
    private String medicament;
    private String dosage;
    private String voie;
    private LocalDateTime datePrevu;
    private boolean administre;
    private LocalDateTime dateAdministration;
    private String observation;
}
