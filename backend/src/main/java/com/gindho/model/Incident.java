package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "incidents")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class Incident extends BaseEntity {
    @Column(nullable=false, length=255) private String titre;
    @Column(length=2000) private String description;
    @Column(nullable=false, length=100) private String typeIncident; // MEDICAL, MEDICAMENTEUX, SECURITE
    @Column(nullable=false, length=50) private String gravite; // FAIBLE, MOYENNE, CRITIQUE
    @Column(nullable=false) private LocalDateTime dateDeclaration;
    @Column(length=2000) private String actionCorrective;
    @Column(nullable=false) private boolean resolu = false;
}