package com.gindho.dto;
import lombok.*; import java.time.LocalDate;
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class EquipementDto {
    private Long id; private String nom; private String modele; private String numeroSerie;
    private String statut; private LocalDate dateAchat;
    private LocalDate dateDerniereMaintenance; private LocalDate dateProchaineMaintenance;
}