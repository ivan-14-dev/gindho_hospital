package com.gindho.pharmacy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindho.kafka.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j @Component @RequiredArgsConstructor
public class KafkaConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notification", groupId = "notification-service")
    public void consumeNotification(String message) {
        log.info("Received notification event: {}", message);
    }

    @KafkaListener(topics = "patient", groupId = "notification-service")
    public void consumePatientEvent(String message) {
        log.info("Received patient event: {}", message);
    }
}