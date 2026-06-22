package com.gindho.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maladies")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Maladie extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private boolean actif = true;

    @ElementCollection
    @CollectionTable(
            name = "maladie_symptomes",
            joinColumns = @JoinColumn(name = "maladie_id")
    )
    @Column(name = "symptome", nullable = false)
    private Set<String> symptomes = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "maladie_analyse_criteres",
            joinColumns = @JoinColumn(name = "maladie_id")
    )
    private Set<MaladieAnalyseCritere> analyseCriteres = new HashSet<>();
}
