package com.gindho.patient.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "documents_patient")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DocumentPatient extends BaseEntity {
    private Long patientId;
    private String type;
    private String nom;
    @Column(length = 2000) private String url;
    private LocalDateTime uploadedAt;
}