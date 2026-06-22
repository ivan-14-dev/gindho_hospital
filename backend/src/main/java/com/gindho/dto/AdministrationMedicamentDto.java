package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdministrationMedicamentDto {
    private Long id;
    private String medicament;
    private String posologie;
    private String voieAdministration;
    private LocalDateTime dateAdministration;
    private boolean administre;
    private String notes;
    private Long patientId;
    private Long hospitalisationId;
    private String patientNom;
}
