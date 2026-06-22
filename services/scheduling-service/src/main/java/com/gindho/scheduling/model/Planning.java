package com.gindho.scheduling.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="plannings") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Planning extends BaseEntity {
    private int mois; private int annee; private String service;
    private boolean valide; private Long validePar; private LocalDateTime dateValidation;
}