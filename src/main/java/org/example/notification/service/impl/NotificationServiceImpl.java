package org.example.notification.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification.dto.request.BulkNotificationRequest;
import org.example.notification.dto.request.SingleNotificationRequest;
import org.example.notification.dto.response.BulkNotificationResponse;
import org.example.notification.dto.response.NotificationResponse;
import org.example.notification.exceptions.InvalidChannelException;
import org.example.notification.exceptions.UserNotFoundException;
import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationStatus;
import org.example.notification.model.User;
import org.example.notification.queue.NotificationPublisher;
import org.example.notification.repository.NotificationRepository;
import org.example.notification.repository.UserRepository;
import org.example.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Core service responsible for validating, creating,
 * scheduling, and publishing notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final UserRepository userRepository;
    private final NotificationPublisher publisher;

    @Override
    public NotificationResponse send(SingleNotificationRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() ->
                new UserNotFoundException(request.getUserId()));
        if (!user.getPreferredChannels().contains(request.getChannel())) {
            throw new InvalidChannelException(request.getChannel().name());
        }

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setUserId(request.getUserId());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setChannel(request.getChannel());
        notification.setPriority(request.getPriority());
        notification.setScheduledAt(request.getScheduledAt());

        if (notification.getScheduledAt() != null && notification.getScheduledAt().isAfter(LocalDateTime.now())) {
            notification.setStatus(NotificationStatus.SCHEDULED);
        } else {
            notification.setStatus(NotificationStatus.PENDING);
        }

        repository.save(notification);

        if (notification.getStatus() == NotificationStatus.PENDING) {
            publisher.publish(notification);
        }

        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getStatus()
        );
    }

    @Override
    public BulkNotificationResponse sendBulk(BulkNotificationRequest request) {
        log.info("Sending Bulk notifications..");
        int success = 0;
        int failed = 0;
        for (String userId : request.getUserIds()) {
            try {
                send(request.toNotificationRequest(userId));
                success++;
            } catch (Exception ex) {
                failed++;
            }
        }
        return new BulkNotificationResponse(
                request.getUserIds().size(),
                success,
                failed);
    }
}