package org.example.notification.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.model.Notification;
import org.springframework.stereotype.Component;

/**
 * Publishes notifications to the processing queue.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

    private final NotificationQueue queue;

    public void publish(Notification notification) {
        log.info("Publishing notification id={}", notification.getNotificationId());
        queue.publish(notification);
    }
}