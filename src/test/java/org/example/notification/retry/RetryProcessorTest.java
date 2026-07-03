package org.example.notification.retry;

import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationStatus;
import org.example.notification.queue.NotificationPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RetryProcessorTest {

    private ExponentialBackoffStrategy strategy;
    private NotificationPublisher publisher;
    private RetryProcessor retryProcessor;

    @BeforeEach
    void setup() {
        strategy = mock(ExponentialBackoffStrategy.class);
        publisher = mock(NotificationPublisher.class);
        retryProcessor = new RetryProcessor(strategy, publisher);
    }

    @Test
    void shouldRetryNotification() {
        Notification notification = new Notification();
        notification.setNotificationId("n1");
        notification.setRetryCount(0);
        when(strategy.getDelay(1)).thenReturn(100L);

        retryProcessor.retry(notification);

        assertEquals(1, notification.getRetryCount());

        assertEquals(NotificationStatus.PENDING, notification.getStatus());

        verify(publisher, times(1))
                .publish(notification);
    }

    @Test
    void shouldIncrementRetryCount() {
        Notification notification = new Notification();

        notification.setNotificationId("n1");
        notification.setRetryCount(0);

        when(strategy.getDelay(1)).thenReturn(100L);

        retryProcessor.retry(notification);
        assertEquals(1, notification.getRetryCount());
    }

    @Test
    void shouldRepublishNotificationAfterRetry() {
        Notification notification = new Notification();
        notification.setNotificationId("n1");
        notification.setRetryCount(0);

        when(strategy.getDelay(1)).thenReturn(100L);

        retryProcessor.retry(notification);
        verify(publisher, times(1)).publish(notification);
    }
}