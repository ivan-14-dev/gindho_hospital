package com.gindho.audit;
import lombok.*; import java.time.LocalDateTime;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditEvent {
    private String serviceName; private String action; private String resource;
    private String resourceId; private String actor; private String ipAddress;
    private String details; private LocalDateTime timestamp = LocalDateTime.now();
}