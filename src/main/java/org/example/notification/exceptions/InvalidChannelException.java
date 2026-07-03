package org.example.notification.exceptions;

public class InvalidChannelException extends RuntimeException {
    public InvalidChannelException(String channel) {
        super("Channel not enabled: " + channel);
    }
}