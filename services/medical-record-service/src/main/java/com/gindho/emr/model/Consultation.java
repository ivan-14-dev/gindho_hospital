package com.gindho.emr.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation extends BaseEntity {

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long medecinId;

    @Column(nullable = false)
    private LocalDateTime dateConsultation;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Column(columnDefinition = "TEXT")
    private String conclusion;

    @Column(nullable = false)
    private String type; // CONSULTATION, URGENCE, HOSPITALISATION
}