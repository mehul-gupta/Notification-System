package org.example.notification.channel.factory;

import org.example.notification.channel.NotificationChannelHandler;
import org.example.notification.channel.EmailChannelHandler;
import org.example.notification.channel.PushChannelHandler;
import org.example.notification.channel.SmsChannelHandler;
import org.example.notification.model.enums.NotificationChannel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChannelFactoryTest {

    @Test
    void shouldReturnEmailHandler() {
        NotificationChannelHandler email = new EmailChannelHandler();
        NotificationChannelHandler sms = new SmsChannelHandler();
        NotificationChannelHandler push = new PushChannelHandler();

        ChannelFactory factory = new ChannelFactory(List.of(email, sms, push));

        NotificationChannelHandler handler = factory.getHandler(NotificationChannel.EMAIL);
        assertInstanceOf(EmailChannelHandler.class, handler);
    }

    @Test
    void shouldReturnSmsHandler() {
        NotificationChannelHandler email = new EmailChannelHandler();
        NotificationChannelHandler sms = new SmsChannelHandler();
        NotificationChannelHandler push = new PushChannelHandler();

        ChannelFactory factory = new ChannelFactory(List.of(email, sms, push));

        NotificationChannelHandler handler = factory.getHandler(NotificationChannel.SMS);

        assertInstanceOf(SmsChannelHandler.class, handler);
    }
}