package org.example.notification.channel.factory;

import org.example.notification.channel.NotificationChannelHandler;
import org.example.notification.model.enums.NotificationChannel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory responsible for resolving the appropriate
 * notification channel handler dynamically.
 */
@Component
public class ChannelFactory {
    private final Map<NotificationChannel, NotificationChannelHandler> handlers;

    public ChannelFactory(List<NotificationChannelHandler> handlerList) {
        handlers = handlerList.stream().collect(Collectors.toMap(
                        NotificationChannelHandler::getChannel,
                        Function.identity()));
    }

    public NotificationChannelHandler getHandler(NotificationChannel channel) {
        return handlers.get(channel);
    }
}