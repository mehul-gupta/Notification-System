package org.example.notification.channel;

import lombok.extern.slf4j.Slf4j;
import org.example.notification.dto.response.DeliveryResponse;
import org.example.notification.model.Notification;
import org.example.notification.model.enums.NotificationChannel;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsChannelHandler implements NotificationChannelHandler {

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public DeliveryResponse send(Notification notification) {
        log.info("Sending SMS to userId={}", notification.getUserId());
        log.info("Subject: {}", notification.getTitle());
        log.info("Body: {}", notification.getMessage());
        return DeliveryResponse.success();
    }
}