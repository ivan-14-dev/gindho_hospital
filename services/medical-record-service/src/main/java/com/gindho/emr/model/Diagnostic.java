package com.gindho.emr.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diagnostics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnostic extends BaseEntity {

    @Column(nullable = false)
    private Long consultationId;

    @Column(nullable = false)
    private String codeCim10;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String type; // PRINCIPAL, ASSOCIE, DIFFERENTIEL
}