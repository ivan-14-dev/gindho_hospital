package com.gindho.authorization.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private String action; // READ, WRITE, DELETE

    @Column(length = 500)
    private String description;
}