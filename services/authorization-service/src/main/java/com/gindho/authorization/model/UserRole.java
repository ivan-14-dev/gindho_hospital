package com.gindho.authorization.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String userId;

    @Column(nullable = false)
    private Long roleId;

    @Column(length = 100)
    private String organisationId;

    @Column(length = 100)
    private String hopitalId;

    @Column(length = 100)
    private String departementId;

    @Column(name = "expires_at")
    private java.time.LocalDateTime expiresAt;
}