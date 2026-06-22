package com.gindho.billing.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*; import lombok.*;
import java.math.BigDecimal; import java.time.LocalDate;

@Entity @Table(name = "revenus")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Revenu extends BaseEntity {
    private LocalDate date;
    private BigDecimal montantTotal;
    private int consultations;
    private int hospitalisations;
    private int analyses;
    private int pharmacie;
    private int autres;
}