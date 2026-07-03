package org.example.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationStatus;
import org.example.notification.queue.NotificationPublisher;
import org.example.notification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Periodically polls scheduled notifications
 * and publishes eligible notifications to queue.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository repository;

    private final NotificationPublisher publisher;

    @Scheduled(fixedDelay = 5000)
    public void processScheduledNotifications() {
        log.info("Checking scheduled notifications...");
        List<Notification> notifications = repository.findScheduledNotifications(LocalDateTime.now());

        for (Notification notification : notifications) {
            notification.setStatus(NotificationStatus.PENDING);
            repository.save(notification);
            publisher.publish(notification);
        }
    }
}