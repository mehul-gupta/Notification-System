package org.example.notification.channel;

import org.example.notification.dto.response.DeliveryResponse;
import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationChannel;

/**
 * Contract for all notification channel handlers.
 */
public interface NotificationChannelHandler {
    NotificationChannel getChannel();
    DeliveryResponse send(Notification notification);
}