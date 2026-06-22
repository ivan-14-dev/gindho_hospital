package com.gindho.audit.model;
import com.gindho.base.BaseEntity; import jakarta.persistence.*; import lombok.*;
import java.time.LocalDateTime;
@Entity @Table(name = "audit_logs") @Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuditLog extends BaseEntity {
    private String serviceSource; private String action; private String resource;
    private String resourceId; private String acteur; private String ipAdresse;
    @Column(columnDefinition = "TEXT") private String details;
    private LocalDateTime dateAction;
}