package com.gindho.scheduling.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="gardes") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Garde extends BaseEntity {
    private Long employeId; private LocalDateTime dateDebut; private LocalDateTime dateFin;
    @Enumerated(EnumType.STRING) private TypeGarde typeGarde;
    @Enumerated(EnumType.STRING) private StatutGarde statut;
    private String service; private String notes;
}