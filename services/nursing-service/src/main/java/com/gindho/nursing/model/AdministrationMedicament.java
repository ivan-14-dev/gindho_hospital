package com.gindho.nursing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "administrations_medicaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrationMedicament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private Long prescriptionId;

    @Column(nullable = false)
    private Long medicamentId;

    @Column(nullable = false)
    private String dosage;

    private String voieAdministration;

    @Column(nullable = false)
    private LocalDateTime dateProgrammee;

    private LocalDateTime dateAdministree;

    private Boolean administre = false;

    private Long administrePar;

    private String observation;
}