package org.example.notification.service;

import org.example.notification.dto.request.BulkNotificationRequest;
import org.example.notification.dto.response.BulkNotificationResponse;
import org.example.notification.dto.request.SingleNotificationRequest;
import org.example.notification.dto.response.NotificationResponse;

public interface NotificationService {
    NotificationResponse send(SingleNotificationRequest request);
    BulkNotificationResponse sendBulk(BulkNotificationRequest request);
}