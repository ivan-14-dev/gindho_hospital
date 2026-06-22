package com.gindho.teleconsultation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "teleconsultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teleconsultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long medecinId;

    @Column(nullable = false)
    private LocalDateTime dateConsultation;

    private String statut;
    private String motif;
    private String lienVisio;
    private String notes;
    private LocalDateTime dateCreation;
}