package com.gindho.nursing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "plans_soins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanSoin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private Long hospitalisationId;

    @Column(nullable = false)
    private String soin;

    private String instructions;

    private String notes;

    private Boolean realise = false;

    private LocalDateTime dateRealisation;

    private Long realisePar;

    @Column(nullable = false)
    private LocalDateTime datePlanification;

    @Column(nullable = false)
    private Long planifiePar;
}