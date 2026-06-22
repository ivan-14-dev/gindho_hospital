package com.gindho.dto;
import lombok.*; import java.time.LocalDateTime;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class AmbulanceDto {
    private Long id; private String immatriculation; private String statut;
    private Double derniereLatitude; private Double derniereLongitude; private LocalDateTime derniereMiseAJour;
}