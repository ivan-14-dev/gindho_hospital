package com.gindho.patient.dto;

import lombok.*;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private Long id;
    private Long patientId;

    @NotBlank(message = "Le type de document est obligatoire")
    @Size(max = 80, message = "Le type de document doit contenir au maximum 80 caractères")
    private String type;

    @NotBlank(message = "Le nom du document est obligatoire")
    @Size(max = 200, message = "Le nom du document doit contenir au maximum 200 caractères")
    private String nom;

    @Size(max = 2000, message = "L'URL doit contenir au maximum 2000 caractères")
    private String url;

    private LocalDateTime uploadedAt;
}
