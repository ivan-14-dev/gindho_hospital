package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
}
