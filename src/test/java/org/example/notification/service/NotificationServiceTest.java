package org.example.notification.service;

import org.example.notification.dto.request.SingleNotificationRequest;
import org.example.notification.dto.response.NotificationResponse;
import org.example.notification.exceptions.InvalidChannelException;
import org.example.notification.exceptions.UserNotFoundException;
import org.example.notification.model.Notification;
import org.example.notification.model.User;
import org.example.notification.model.enums.NotificationChannel;
import org.example.notification.model.enums.NotificationPriority;
import org.example.notification.model.enums.NotificationStatus;
import org.example.notification.queue.NotificationPublisher;
import org.example.notification.repository.NotificationRepository;
import org.example.notification.repository.UserRepository;
import org.example.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private NotificationPublisher publisher;

    private NotificationServiceImpl service;

    @BeforeEach
    void setup() {
        notificationRepository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        publisher = mock(NotificationPublisher.class);
        service = new NotificationServiceImpl(
                notificationRepository,
                userRepository,
                publisher);
    }

    @Test
    void shouldSendHighPriorityNotification() {
        User user = new User();
        user.setUserId("u1");
        user.setPreferredChannels(Set.of(NotificationChannel.EMAIL));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        SingleNotificationRequest request =
                SingleNotificationRequest.builder()
                        .userId("u1")
                        .title("Urgent")
                        .message("High priority message")
                        .channel(NotificationChannel.EMAIL)
                        .priority(NotificationPriority.HIGH)
                        .build();

        NotificationResponse response = service.send(request);
        assertNotNull(response);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();

        assertEquals(NotificationPriority.HIGH, saved.getPriority());
        assertEquals(NotificationStatus.PENDING, saved.getStatus());

        verify(publisher, times(1)).publish(any(Notification.class));
    }

    @Test
    void shouldScheduleNotification() {
        User user = new User();
        user.setUserId("u1");
        user.setPreferredChannels(Set.of(NotificationChannel.EMAIL));

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        SingleNotificationRequest request = SingleNotificationRequest.builder()
                .userId("u1")
                .title("Scheduled")
                .message("Future message")
                .channel(NotificationChannel.EMAIL)
                .scheduledAt(LocalDateTime.now().plusHours(1))
                .build();

        service.send(request);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();

        assertEquals(NotificationStatus.SCHEDULED, saved.getStatus());
        verify(publisher, never()).publish(any(Notification.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        SingleNotificationRequest request = SingleNotificationRequest.builder()
                .userId("u1")
                .title("Test")
                .message("Test")
                .channel(NotificationChannel.EMAIL)
                .build();

        assertThrows(UserNotFoundException.class, () -> service.send(request));
        verify(publisher, never()).publish(any(Notification.class));
    }

    @Test
    void shouldThrowExceptionForUnsupportedChannel() {
        User user = new User();
        user.setUserId("u1");
        user.setPreferredChannels(Set.of(NotificationChannel.SMS));

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        SingleNotificationRequest request = SingleNotificationRequest.builder()
                .userId("u1")
                .title("Test")
                .message("Test")
                .channel(NotificationChannel.EMAIL)
                .build();

        assertThrows(InvalidChannelException.class, () -> service.send(request));
        verify(publisher, never()).publish(any(Notification.class));
    }

    @Test
    void shouldUseDefaultPriorityWhenNotProvided() {
        User user = new User();
        user.setUserId("u1");
        user.setPreferredChannels(Set.of(NotificationChannel.EMAIL));

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        SingleNotificationRequest request = SingleNotificationRequest.builder()
                .userId("u1")
                .title("Default")
                .message("Default priority")
                .channel(NotificationChannel.EMAIL)
                .build();

        service.send(request);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(NotificationPriority.MEDIUM, saved.getPriority());
    }
}