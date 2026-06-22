package com.gindho.patient.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MedecinDto {
    private Long id;
    private String numeroOrdre;
    private String specialisation;
    private String telephoneCabinet;
    private Boolean disponible;
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private LocalDateTime creeLe;
    private LocalDateTime misAJourLe;
}
