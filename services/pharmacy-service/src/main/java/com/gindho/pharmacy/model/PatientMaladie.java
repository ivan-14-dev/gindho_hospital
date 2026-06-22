package com.gindho.pharmacy.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patient_maladies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientMaladie extends BaseEntity {
    private Long patientId;
    private Long maladieId;
    private String statut;
}
