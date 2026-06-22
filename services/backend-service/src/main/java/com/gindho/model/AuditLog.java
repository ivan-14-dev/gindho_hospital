package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {
    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entite;

    @Column(name = "entite_id")
    private String entiteId;

    @Column(length = 2000)
    private String details;

    @Column(name = "ip_adresse")
    private String ipAdresse;
}
