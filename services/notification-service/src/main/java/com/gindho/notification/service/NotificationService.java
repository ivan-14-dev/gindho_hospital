package com.gindho.notification.service;

import com.gindho.notification.dto.NotificationDto;
import com.gindho.notification.model.Notification;
import com.gindho.notification.repository.NotificationRepository;
import com.gindho.kafka.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;
    private final EventProducer eventProducer;
}
