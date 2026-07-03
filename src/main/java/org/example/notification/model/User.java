package org.example.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.notification.model.enums.NotificationChannel;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private String userId;
    private String name;

    /** Required for EMAIL channel. */
    private String email;
    /** Required for SMS channel. */
    private String phoneNumber;
    /** FCM / APNs token; required for PUSH channel. */
    private String deviceToken;

    private Set<NotificationChannel> preferredChannels;
    private LocalDateTime createdAt;
}