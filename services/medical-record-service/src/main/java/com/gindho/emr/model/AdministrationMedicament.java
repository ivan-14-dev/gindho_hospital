package com.gindho.emr.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "administrations_medicaments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdministrationMedicament extends BaseEntity {
    private Long patientId;
    private Long hospitalisationId;
    private String medicament;
    private String dosage;
    private String voie;
    private LocalDateTime datePrevu;
    private boolean administre;
    private LocalDateTime dateAdministration;
    @Column(columnDefinition = "TEXT")
    private String observation;
}
