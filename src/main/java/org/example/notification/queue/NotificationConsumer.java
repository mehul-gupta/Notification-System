package org.example.notification.queue;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.exceptions.InvalidChannelException;
import org.example.notification.exceptions.UserNotFoundException;
import org.example.notification.model.Notification;
import org.example.notification.worker.NotificationWorker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Continuously consumes notifications from queue
 * and delegates processing to worker threads.
 */
@Component
@Slf4j
public class NotificationConsumer {

    private final NotificationQueue queue;
    private final NotificationWorker worker;
    private final ThreadPoolTaskExecutor executor;

    public NotificationConsumer(NotificationQueue queue, NotificationWorker worker,
                                @Qualifier("notificationConsumerExecutor") ThreadPoolTaskExecutor executor) {
        this.queue = queue;
        this.worker = worker;
        this.executor = executor;
    }

    @PostConstruct
    public void startConsumer() {
        executor.submit(() -> {
            log.info("Notification consumer started");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Notification notification = queue.consume();
                    if (notification == null) {
                        log.info("Consumer interrupted during shutdown");
                        break;
                    }

                    log.info("Consumed notification id={}", notification.getNotificationId());
                    worker.process(notification);
                } catch (UserNotFoundException ex) {
                    log.warn("User not found: {}", ex.getMessage());
                } catch (InvalidChannelException ex) {
                    log.warn("Invalid channel: {}", ex.getMessage());
                } catch (Exception ex) {
                    log.error("Unexpected error while processing notification", ex);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down notification consumer");
        executor.shutdown();
        try {
            if (!executor.getThreadPoolExecutor().awaitTermination(5, TimeUnit.SECONDS)) {
                executor.getThreadPoolExecutor().shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.getThreadPoolExecutor().shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}