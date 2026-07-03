package org.example.notification.repository;

import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryNotificationRepository implements NotificationRepository {

    private final Map<String, Notification> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Notification notification) {
        storage.put(notification.getNotificationId(), notification);
    }

    @Override
    public Optional<Notification> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Notification> findScheduledNotifications(LocalDateTime currentTime) {
        return storage.values().stream().filter(notification ->
                        notification.getStatus() == NotificationStatus.SCHEDULED &&
                                notification.getScheduledAt().isBefore(currentTime)).toList();
    }
}