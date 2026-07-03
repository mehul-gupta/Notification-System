package org.example.notification.retry;

import lombok.extern.slf4j.Slf4j;
import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationStatus;
import org.example.notification.queue.NotificationPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetryProcessor {

    private static final int MAX_RETRIES = 3;
    private final RetryStrategy strategy;
    private final NotificationPublisher publisher;

    public RetryProcessor(RetryStrategy strategy, NotificationPublisher publisher) {
        this.strategy = strategy;
        this.publisher = publisher;
    }

    public void retry(Notification notification) {
        if (notification.getRetryCount() >= MAX_RETRIES) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Notification {} failed after {} retries", notification.getNotificationId(),
                    notification.getRetryCount());
            return;
        }

        notification.setRetryCount(notification.getRetryCount() + 1);
        long delay = strategy.getDelay(notification.getRetryCount());
        notification.setStatus(NotificationStatus.PROCESSING);
        log.info("Retrying notification {} after {} ms. Attempt {}", notification.getNotificationId(),
                delay, notification.getRetryCount());

        try {
            Thread.sleep(delay);
            notification.setStatus(NotificationStatus.PENDING);
            publisher.publish(notification);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Retry interrupted for notification {}", notification.getNotificationId(), ex);
        }
    }
}