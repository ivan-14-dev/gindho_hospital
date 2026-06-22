package com.gindho.asset.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDateTime;
@Entity @Table(name="maintenances") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Maintenance extends BaseEntity {
    private Long equipementId; private LocalDateTime dateDebut; private LocalDateTime dateFin;
    @Enumerated(EnumType.STRING) private TypeMaintenance typeMaintenance;
    @Column(columnDefinition="TEXT") private String description;
    private BigDecimal cout; private String technicien;
}