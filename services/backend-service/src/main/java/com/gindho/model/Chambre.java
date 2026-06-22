package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chambres", indexes = {
        @Index(name = "idx_chambres_actif", columnList = "actif")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Chambre extends BaseEntity {

    @Column(name = "numero_chambre", nullable = false, unique = true)
    private String numeroChambre;

    @Column(nullable = false)
    private boolean actif = true;

    @OneToMany(mappedBy = "chambre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lit> lits = new ArrayList<>();
}
