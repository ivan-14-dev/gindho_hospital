package com.gindho.prescription.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="ordonnances") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Ordonnance extends BaseEntity {
    private Long patientId; private Long medecinId; private Long consultationId;
    private LocalDateTime datePrescription; private String diagnostic;
    @Column(columnDefinition="TEXT") private String instructions;
    @Enumerated(EnumType.STRING) private StatutOrdonnance statut;
}