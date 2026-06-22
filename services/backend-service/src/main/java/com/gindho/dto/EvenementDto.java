package com.gindho.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class EvenementDto {
    private Long id; private String titre; private String description;
    private String typeEvenement; private LocalDateTime dateDebut;
    private LocalDateTime dateFin; private boolean valide;
}