package com.gindho.patient.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medecins")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Medecin extends BaseEntity {

    @Column(name = "numero_ordre", unique = true, nullable = false, length = 50)
    private String numeroOrdre;

    @Column(length = 100)
    private String specialisation;

    @Column(length = 20)
    private String telephoneCabinet;

    @Column(nullable = false)
    private boolean disponible = true;

    @Column(name = "user_id")
    private Long userId;
}
