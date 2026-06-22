package com.gindho.model;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDate;

@Entity @Table(name = "audits_qualite")
@Data @Builder @EqualsAndHashCode(callSuper=true) @NoArgsConstructor @AllArgsConstructor
public class AuditQualite extends BaseEntity {
    @Column(nullable=false, length=255) private String titre;
    @Column(length=2000) private String description;
    @Column(nullable=false, length=100) private String typeAudit; // CLINIQUE, SECURITE, INDICATEUR
    private LocalDate dateAudit;
    private int score; // 0-100
    @Column(length=2000) private String recommandations;
}