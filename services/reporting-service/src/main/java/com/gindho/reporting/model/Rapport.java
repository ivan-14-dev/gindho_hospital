package com.gindho.reporting.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="rapports") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Rapport extends BaseEntity {
    @Column(unique=true) private String code; private String titre; private String type;
    private String format;
    @Column(columnDefinition="TEXT") private String parametres;
    @Enumerated(EnumType.STRING) private StatutRapport statut;
    private LocalDateTime dateGeneration; private Long generePar;
}