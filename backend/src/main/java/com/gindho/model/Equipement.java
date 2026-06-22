package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "equipements")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class Equipement extends BaseEntity {
    @Column(nullable=false, length=255) private String nom;
    @Column(length=255) private String modele;
    @Column(length=255) private String numeroSerie;
    @Column(nullable=false, length=100) private String statut; // OPERATIONNEL, EN_PANNE, EN_MAINTENANCE
    private LocalDate dateAchat;
    private LocalDate dateDerniereMaintenance;
    private LocalDate dateProchaineMaintenance;
}