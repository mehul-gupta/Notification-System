package org.example.notification.worker;

import org.example.notification.channel.NotificationChannelHandler;
import org.example.notification.channel.factory.ChannelFactory;
import org.example.notification.dto.response.DeliveryResponse;
import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationChannel;
import org.example.notification.model.enums.NotificationStatus;
import org.example.notification.repository.NotificationRepository;
import org.example.notification.retry.RetryProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NotificationWorkerTest {

    private ChannelFactory factory;
    private RetryProcessor retryProcessor;
    private NotificationRepository repository;
    private NotificationWorker worker;

    @BeforeEach
    void setup() {
        factory = mock(ChannelFactory.class);
        retryProcessor = mock(RetryProcessor.class);
        repository = mock(NotificationRepository.class);
        worker = new NotificationWorker(factory, retryProcessor, repository);
    }

    @Test
    void shouldMarkNotificationAsSent() {
        Notification notification = new Notification();
        notification.setChannel(NotificationChannel.EMAIL);
        NotificationChannelHandler handler = mock(NotificationChannelHandler.class);

        when(factory.getHandler(NotificationChannel.EMAIL)).thenReturn(handler);
        when(handler.send(notification)).thenReturn(new DeliveryResponse(true, "success"));

        worker.process(notification);

        assertEquals(NotificationStatus.SENT, notification.getStatus());

        verify(repository, times(1)).save(notification);
    }

    @Test
    void shouldRetryFailedNotification() {
        Notification notification = new Notification();
        notification.setChannel(NotificationChannel.EMAIL);
        NotificationChannelHandler handler = mock(NotificationChannelHandler.class);

        when(factory.getHandler(NotificationChannel.EMAIL)).thenReturn(handler);
        when(handler.send(notification)).thenReturn(new DeliveryResponse(false, "failure"));

        worker.process(notification);

        assertEquals(NotificationStatus.FAILED, notification.getStatus());

        verify(retryProcessor, times(1)).retry(notification);
    }
}