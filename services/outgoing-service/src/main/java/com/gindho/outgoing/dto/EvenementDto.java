package com.gindho.outgoing.dto;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EvenementDto {
    private Long id; private String titre; private String description; private String type;
    private LocalDateTime dateDebut; private LocalDateTime dateFin; private String lieu;
    private String organisateur; private String statut; private String contenuPublication;
    private boolean publique; private String imageUrl; private String lienInscription;
}
