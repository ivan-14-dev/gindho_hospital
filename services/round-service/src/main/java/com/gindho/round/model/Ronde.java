package com.gindho.round.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name="rondes") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Ronde extends BaseEntity {
    private Long patientId; private Long medecinResponsableId;
    private LocalDateTime dateRonde; private String service;
    @Enumerated(EnumType.STRING) private StatutRonde statut;
    @Column(columnDefinition="TEXT") private String observations;
}