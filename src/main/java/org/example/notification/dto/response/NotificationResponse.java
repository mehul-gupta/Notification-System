package org.example.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.notification.model.enums.NotificationStatus;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private String notificationId;
    private NotificationStatus status;
}