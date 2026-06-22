package com.gindho.authorization.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_template_permissions")
@Getter
@Setter
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
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String scope;
    private String conditionType;
}
