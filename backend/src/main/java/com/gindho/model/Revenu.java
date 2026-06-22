package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "revenus")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Revenu extends BaseEntity {
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;
    
    @Column(nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TypeRevenu typeRevenu;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    private LocalDateTime dateRevenu;
    
    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;
    
    public enum TypeRevenu {
        CONSULTATION,
        EXAMEN,
        PROCEDURE,
        MEDICAMENT,
        AUTRE
    }
}
