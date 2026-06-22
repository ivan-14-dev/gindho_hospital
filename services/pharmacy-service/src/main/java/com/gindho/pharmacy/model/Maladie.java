package com.gindho.pharmacy.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "maladies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Maladie extends BaseEntity {
    private String code;
    private String nom;
    @Column(columnDefinition = "TEXT")
    private String description;
    private boolean active = true;
}
