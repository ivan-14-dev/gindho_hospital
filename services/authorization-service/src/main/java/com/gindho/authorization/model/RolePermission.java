package com.gindho.authorization.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission extends BaseEntity {

    @Column(nullable = false)
    private Long roleId;

    @Column(nullable = false)
    private Long permissionId;

    @Column(length = 20)
    private String conditionType; // ALL, OWN, ASSIGNED, DEPARTMENT

    @Column(length = 100)
    private String scope;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;
}