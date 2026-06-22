package com.gindho.notification.controller;

import com.gindho.base.ApiResponse;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventProducer;
import com.gindho.kafka.EventType;
import com.gindho.notification.dto.NotificationDto;
import com.gindho.notification.model.Notification;
import com.gindho.notification.model.Notification.CanalNotification;
import com.gindho.notification.model.Notification.StatutNotification;
import com.gindho.notification.repository.NotificationRepository;
import com.gindho.notification.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;
    private final EventProducer eventProducer;
    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<NotificationDto>> send(@RequestBody NotificationDto dto) {
        Notification n = new Notification();
        n.setUserId(dto.getUserId());
        n.setDestinataire(dto.getDestinataire());
        n.setSujet(dto.getTitre());
        n.setContenu(dto.getMessage());
        n.setCanal(CanalNotification.valueOf(dto.getCanal() != null ? dto.getCanal() : "EMAIL"));
        n.setStatut(StatutNotification.EN_ATTENTE);
        n.setDateEnvoi(LocalDateTime.now());
        n = repository.save(n);

        eventProducer.publish("notification", BaseEvent.builder()
                .eventType(EventType.NOTIFICATION_SENT)
                .source("notification-service")
                .payload(dto)
                .build());

        if (dto.isSendEmail() && dto.getEmail() != null) {
            mailService.sendEmail(dto.getEmail(), dto.getTitre(), dto.getMessage());
        }

        n.setStatut(StatutNotification.ENVOYE);
        repository.save(n);

        return ResponseEntity.ok(ApiResponse.ok("Notification envoyée", toDto(n)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = repository.findByUserIdOrderByDateEnvoiDesc(userId);
        List<NotificationDto> dtos = notifications.stream().map(this::toDto).toList();
        return ResponseEntity.ok(ApiResponse.ok("Notifications récupérées", dtos));
    }

    @PostMapping("/broadcast")
    public ResponseEntity<ApiResponse<Void>> broadcast(@RequestBody NotificationDto dto) {
        Notification n = new Notification();
        n.setDestinataire(dto.getDestinataire());
        n.setSujet(dto.getTitre());
        n.setContenu(dto.getMessage());
        n.setCanal(CanalNotification.valueOf(dto.getCanal() != null ? dto.getCanal() : "EMAIL"));
        n.setStatut(StatutNotification.ENVOYE);
        n.setDateEnvoi(LocalDateTime.now());
        repository.save(n);

        mailService.sendEmail(dto.getEmail(), dto.getTitre(), dto.getMessage());
        return ResponseEntity.ok(ApiResponse.ok("Notification broadcastée", null));
    }

    @MessageMapping("/notify")
    @SendTo("/topic/notifications")
    public NotificationDto broadcastNotification(NotificationDto dto) {
        return dto;
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .destinataire(n.getDestinataire())
                .titre(n.getSujet())
                .message(n.getContenu())
                .canal(n.getCanal() != null ? n.getCanal().name() : null)
                .statut(n.getStatut() != null ? n.getStatut().name() : null)
                .dateEnvoi(n.getDateEnvoi())
                .build();
    }
}