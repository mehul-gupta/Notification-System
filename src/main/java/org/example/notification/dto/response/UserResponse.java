package org.example.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.notification.model.enums.NotificationChannel;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserResponse {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private Set<NotificationChannel> preferredChannels;
}