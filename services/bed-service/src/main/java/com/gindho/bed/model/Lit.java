package com.gindho.bed.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name = "lits") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Lit extends BaseEntity {
    private Long chambreId; private String code; private boolean occupe;
    @Column(name = "patient_id") private Long patientId; private String observation;
}