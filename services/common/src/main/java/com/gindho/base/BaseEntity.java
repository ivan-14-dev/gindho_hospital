package com.gindho.base;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@MappedSuperclass @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "cree_le", nullable = false, updatable = false) private LocalDateTime creeLe;
    @Column(name = "mis_a_jour_le", nullable = false) private LocalDateTime misAJourLe;
    @PrePersist protected void prePersist() { if (creeLe == null) creeLe = LocalDateTime.now(); if (misAJourLe == null) misAJourLe = LocalDateTime.now(); }
    @PreUpdate protected void preUpdate() { misAJourLe = LocalDateTime.now(); }
}