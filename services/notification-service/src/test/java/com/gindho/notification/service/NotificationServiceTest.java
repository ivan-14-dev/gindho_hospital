package com.gindho.notification.service;

import com.gindho.notification.dto.NotificationDto;
import com.gindho.notification.model.Notification;
import com.gindho.notification.repository.NotificationRepository;
import com.gindho.notification.service.MailService;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock private NotificationRepository repository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private NotificationService service;

    @Test void contextLoads() { assertNotNull(service); }
}
