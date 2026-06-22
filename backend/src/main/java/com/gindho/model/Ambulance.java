package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "ambulances")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class Ambulance extends BaseEntity {
    @Column(nullable=false, length=50) private String immatriculation;
    @Column(nullable=false, length=50) private String statut; // DISPONIBLE, EN_MISSION, MAINTENANCE
    @Column(name="derniere_latitude") private Double derniereLatitude;
    @Column(name="derniere_longitude") private Double derniereLongitude;
    @Column(name="derniere_mise_a_jour") private LocalDateTime derniereMiseAJour;
}