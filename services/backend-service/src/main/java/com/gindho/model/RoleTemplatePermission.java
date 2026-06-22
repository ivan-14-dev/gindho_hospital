package com.gindho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_template_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleTemplatePermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private RoleTemplate template;

    @Column(nullable = false)
    private String permission;

    private String ressource;

    private String action;

    @Column(name = "valid_from")
    private java.time.LocalDateTime validFrom;

    @Column(name = "valid_to")
    private java.time.LocalDateTime validTo;

    @Column
    private String scope;

    @Column(name = "condition_type")
    private String conditionType;
}
