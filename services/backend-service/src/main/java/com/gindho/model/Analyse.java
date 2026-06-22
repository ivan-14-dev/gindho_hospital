package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Analyse extends BaseEntity {
    
    @Column(nullable = false)
    private String typeAnalyse;
    
    @Column(nullable = false)
    private String resultat;
    
    @Column(columnDefinition = "TEXT")
    private String observation;
    
    private LocalDateTime dateAnalyse;
    
    private boolean urgent = false;
    
    // Relations
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_medical_id")
    private DossierMedical dossierMedical;
}
