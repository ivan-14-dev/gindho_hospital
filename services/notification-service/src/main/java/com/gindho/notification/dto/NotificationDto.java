package com.gindho.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private Long userId;
    private String destinataire;
    private String titre;
    private String message;
    private String type;
    private String canal;
    private String statut;
    private boolean lu;
    private boolean sendEmail;
    private String email;
    private LocalDateTime dateEnvoi;
}