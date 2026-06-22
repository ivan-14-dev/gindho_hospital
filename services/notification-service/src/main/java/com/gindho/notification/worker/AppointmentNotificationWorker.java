package com.gindho.notification.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindho.kafka.BaseEvent;
import com.gindho.kafka.EventType;
import com.gindho.notification.model.Notification;
import com.gindho.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentNotificationWorker {
    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "appointment", groupId = "notification-service")
    public void consumeAppointment(String message) {
        try {
            BaseEvent event = objectMapper.readValue(message, BaseEvent.class);
            if (EventType.APPOINTMENT_BOOKED.equals(event.getEventType())) {
                Notification notification = new Notification();
                notification.setDestinataire(extractPatientId(event));
                notification.setSujet("Rendez-vous confirmé");
                notification.setContenu("Un rendez-vous a été créé dans GinDHO.");
                notification.setCanal(Notification.CanalNotification.EMAIL);
                notification.setStatut(Notification.StatutNotification.EN_ATTENTE);
                notification.setDateEnvoi(LocalDateTime.now());
                repository.save(notification);
                log.info("Notification queued for appointment event {}", event.getEventId());
            }
        } catch (Exception e) {
            log.warn("Invalid appointment event: {}", e.getMessage());
        }
    }

    private String extractPatientId(BaseEvent event) {
        Object payload = event.getPayload();
        if (payload instanceof Map<?, ?> map && map.get("patientId") != null) {
            return String.valueOf(map.get("patientId"));
        }
        return "patient";
    }
}
