package com.gindho.notification.worker;

import com.gindho.notification.model.Notification;
import com.gindho.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class ReminderScheduler {
    private final NotificationRepository repository;

    @Scheduled(fixedRateString = "${appointment.reminder.fixed-rate-ms:900000}")
    public void remindUpcomingAppointments() {
        log.debug("Appointment reminder worker tick");
    }
}
