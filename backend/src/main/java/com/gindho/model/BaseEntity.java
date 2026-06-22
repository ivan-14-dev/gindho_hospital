package com.gindho.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "cree_le", nullable = false, updatable = false)
    private LocalDateTime creeLe;

    @UpdateTimestamp
    @Column(name = "mis_a_jour_le", nullable = false)
    private LocalDateTime misAJourLe;

    @PrePersist
    protected void prePersist() {
        if (creeLe == null) {
            creeLe = LocalDateTime.now();
        }
        if (misAJourLe == null) {
            misAJourLe = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void preUpdate() {
        misAJourLe = LocalDateTime.now();
    }

    // Explicit getters to avoid relying solely on Lombok for IDE/static analysis.
    public LocalDateTime getCreeLe() {
        return creeLe;
    }

    public LocalDateTime getMisAJourLe() {
        return misAJourLe;
    }
}
