package com.gindho.admission.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "hospitalisations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Hospitalisation extends BaseEntity {
    private Long patientId;
    private Long medecinId;
    private Long litId;
    private LocalDateTime dateAdmission;
    private LocalDateTime dateSortie;
    @Enumerated(EnumType.STRING) private StatutHospitalisation statut = StatutHospitalisation.ADMIS;
    @Column(columnDefinition = "TEXT") private String motif;
    private String service;
    @Column(columnDefinition = "TEXT") private String notes;

    public enum StatutHospitalisation { ADMIS, TRANSFERE, SORTI, DECEDE }
}