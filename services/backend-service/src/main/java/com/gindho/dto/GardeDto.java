package com.gindho.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class GardeDto {
    private Long id; private String typeGarde;
    private LocalDateTime dateDebut; private LocalDateTime dateFin;
    private boolean confirmee; private Long medecinId; private String medecinNom;
}