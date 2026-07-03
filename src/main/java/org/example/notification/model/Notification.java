package org.example.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.notification.model.enums.NotificationChannel;
import org.example.notification.model.enums.NotificationPriority;
import org.example.notification.model.enums.NotificationStatus;
import org.example.notification.model.enums.RecurrenceType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Comparable<Notification> {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private NotificationChannel channel;
    private NotificationPriority priority;
    private NotificationStatus status;
    private LocalDateTime scheduledAt;
    private RecurrenceType recurrenceType = RecurrenceType.NONE;
    private int retryCount;
    private LocalDateTime createdAt;

    @Override
    public int compareTo(Notification other) {
        return Integer.compare(
                other.getPriority().getWeight(),
                this.priority.getWeight());
    }
}