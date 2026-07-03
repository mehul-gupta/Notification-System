package org.example.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BulkNotificationResponse {
    private int totalNotifications;
    private int successfulNotifications;
    private int failedNotifications;
}