package com.gindho.emr.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "observations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Observation extends BaseEntity {

    @Column(nullable = false)
    private Long consultationId;

    @Column(nullable = false)
    private String type; // ANAMNESE, EXAMEN_CLINIQUE, EVOLUTION, NOTE

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;
}