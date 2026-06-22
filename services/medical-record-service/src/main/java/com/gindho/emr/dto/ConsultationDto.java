package com.gindho.emr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationDto {

    private Long id;

    @NotNull(message = "Le patient est obligatoire")
    private Long patientId;

    @NotNull(message = "Le médecin est obligatoire")
    private Long medecinId;

    @NotNull(message = "La date de consultation est obligatoire")
    private LocalDateTime dateConsultation;

    private String motif;

    private String conclusion;

    @NotBlank(message = "Le type est obligatoire")
    private String type; // CONSULTATION, URGENCE, HOSPITALISATION

    private LocalDateTime creeLe;

    private LocalDateTime misAJourLe;
}