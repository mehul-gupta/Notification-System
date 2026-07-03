package org.example.notification.queue;

import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationPriority;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationQueueTest {

    @Test
    void shouldConsumeHighPriorityNotificationFirst() {

        NotificationQueue queue = new NotificationQueue();
        Notification low = Notification.builder()
                        .notificationId("n1")
                        .priority(NotificationPriority.LOW)
                        .build();

        Notification high = Notification.builder()
                        .notificationId("n2")
                        .priority(NotificationPriority.HIGH)
                        .build();

        queue.publish(low);
        queue.publish(high);

        Notification consumed = queue.consume();

        assertEquals(NotificationPriority.HIGH, consumed.getPriority());
        assertEquals("n2", consumed.getNotificationId());
    }

    @Test
    void shouldConsumeMediumPriorityBeforeLow() {

        NotificationQueue queue = new NotificationQueue();
        Notification low = Notification.builder()
                        .notificationId("n1")
                        .priority(NotificationPriority.LOW)
                        .build();

        Notification medium = Notification.builder()
                        .notificationId("n2")
                        .priority(NotificationPriority.MEDIUM)
                        .build();

        queue.publish(low);
        queue.publish(medium);

        Notification consumed = queue.consume();

        assertEquals(NotificationPriority.MEDIUM, consumed.getPriority());
        assertEquals("n2", consumed.getNotificationId());
    }
}