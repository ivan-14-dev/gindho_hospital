package com.gindho.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO pour les logs d'audit — les champs sensibles sont masqués.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private LocalDateTime timestamp;
    private String userId;
    private String action;
    private String module;
    private String resourceId;
    private String ipAddress;
    // Pas de querystring brute, pas de body
    private String status;      // SUCCESS / FAILURE
}
