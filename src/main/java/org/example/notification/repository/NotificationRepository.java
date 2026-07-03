package org.example.notification.repository;

import org.example.notification.model.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    void save(Notification notification);
    Optional<Notification> findById(String id);
    List<Notification> findScheduledNotifications(LocalDateTime currentTime);
}