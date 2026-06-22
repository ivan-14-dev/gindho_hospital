package com.gindho.reporting.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="rapports_planifies") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RapportPlanifie extends BaseEntity {
    private Long rapportId; private String cronExpression; private String destinataires;
    private boolean actif;
}