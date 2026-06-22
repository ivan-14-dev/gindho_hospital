package com.gindho.event.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EvenementDto {
    private Long id;
    private String titre;
    private String description;
    private String typeEvenement;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private boolean valide;
}
