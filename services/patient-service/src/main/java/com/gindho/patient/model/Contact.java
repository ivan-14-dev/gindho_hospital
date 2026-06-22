package com.gindho.patient.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "contacts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Contact extends BaseEntity {
    @Column(nullable = false) private Long patientId;
    private String nom; private String telephone; private String relation; private String email;
}