package org.example.notification.worker;

import lombok.RequiredArgsConstructor;
import org.example.notification.channel.NotificationChannelHandler;
import org.example.notification.channel.factory.ChannelFactory;
import org.example.notification.dto.response.DeliveryResponse;
import org.example.notification.model.enums.NotificationStatus;
import org.example.notification.model.Notification;
import org.example.notification.model.enums.RecurrenceType;
import org.example.notification.repository.NotificationRepository;
import org.example.notification.retry.RetryProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Processes notifications by resolving the correct
 * channel handler and triggering delivery.
 */
@Component
@RequiredArgsConstructor
public class NotificationWorker {

    private final ChannelFactory factory;
    private final RetryProcessor retryProcessor;
    private final NotificationRepository repository;

    public void process(Notification notification) {
        NotificationChannelHandler handler = factory.getHandler(notification.getChannel());
        DeliveryResponse response = handler.send(notification);
        if (response.isSuccess()) {
            notification.setStatus(NotificationStatus.SENT);

            // Create next recurring notification schedule if applicable
            if (notification.getRecurrenceType() != RecurrenceType.NONE) {
                Notification nextNotification = createNextRecurringNotification(notification);
                repository.save(nextNotification);
            }
        } else {
            notification.setStatus(NotificationStatus.FAILED);
            retryProcessor.retry(notification);
        }
        repository.save(notification);
    }

    private Notification createNextRecurringNotification(Notification notification) {
        Notification next = new Notification();
        next.setNotificationId(UUID.randomUUID().toString());
        next.setUserId(notification.getUserId());
        next.setTitle(notification.getTitle());
        next.setMessage(notification.getMessage());
        next.setChannel(notification.getChannel());
        next.setPriority(notification.getPriority());
        next.setRecurrenceType(notification.getRecurrenceType());
        next.setStatus(NotificationStatus.SCHEDULED);
        next.setCreatedAt(LocalDateTime.now());
        LocalDateTime nextTime = calculateNextTime(notification);
        next.setScheduledAt(nextTime);
        return next;
    }

    private LocalDateTime calculateNextTime(Notification notification) {
        return switch (notification.getRecurrenceType()) {
            case DAILY -> notification.getScheduledAt().plusDays(1);
            case WEEKLY -> notification.getScheduledAt().plusWeeks(1);
            case MONTHLY -> notification.getScheduledAt().plusMonths(1);
            default -> throw new IllegalArgumentException("Invalid recurrence type");
        };
    }
}