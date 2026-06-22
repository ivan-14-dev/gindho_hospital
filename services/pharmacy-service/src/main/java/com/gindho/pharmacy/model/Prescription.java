package com.gindho.pharmacy.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "prescriptions") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Prescription extends BaseEntity {
    private Long patientId; private Long medecinId; private Long consultationId;
    private LocalDateTime datePrescription; private String medicament;
    private String posologie; private String duree;
    @Column(columnDefinition = "TEXT") private String instructions;
    @Enumerated(EnumType.STRING) private StatutPrescription statut = StatutPrescription.ACTIVE;
    public enum StatutPrescription { ACTIVE, TERMINEE, ANNULEE }
}