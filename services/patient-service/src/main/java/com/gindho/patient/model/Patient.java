package com.gindho.patient.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseEntity {
    @Column(unique = true)
    private String numeroPatient;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(length = 254, unique = true)
    private String email;

    @Column(name = "id_number", length = 100, unique = true)
    private String idNumber;

    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Sexe sexe;

    @Column(length = 30)
    private String telephone;

    @Column(name = "groupe_sanguin", length = 20)
    private String groupeSanguin;

    @Column(length = 20)
    private String taille;

    @Column(length = 100)
    private String ville;

    @Column(columnDefinition = "TEXT")
    private String antecedents;

    @Column(length = 1000)
    private String allergies;

    @Column(length = 1000)
    private String adresse;

    @Column(name = "user_id")
    private Long userId;

    private boolean actif = true;

    @Column(name = "date_archivage")
    private LocalDateTime dateArchivage;

    public enum Sexe {
        HOMME,
        FEMME,
        AUTRE
    }
}
