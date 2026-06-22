package com.gindho.nursing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "signes_vitaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigneVitaux {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private Long hospitalisationId;

    private Double temperature;
    private Integer pulsation;
    private Integer tensionSystolique;
    private Integer tensionDiastolique;
    private Integer frequenceRespiratoire;
    private Double saturationOxygene;
    private String observation;

    @Column(nullable = false)
    private LocalDateTime dateReleve;

    @Column(nullable = false)
    private Long relevePar;
}