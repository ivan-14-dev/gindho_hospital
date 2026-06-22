package com.gindho.patient.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "identifiants_patient")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class IdentifiantPatient extends BaseEntity {
    private Long patientId;
    private String typeIdentifiant;
    private String valeur;
}