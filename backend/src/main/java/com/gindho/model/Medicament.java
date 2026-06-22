package com.gindho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medicaments")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Medicament extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private boolean actif = true;
}
