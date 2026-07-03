package org.example.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.notification.model.enums.NotificationChannel;
import org.example.notification.model.enums.NotificationPriority;
import org.example.notification.model.enums.RecurrenceType;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseNotificationRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    private NotificationChannel channel;

    @Builder.Default
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    private LocalDateTime scheduledAt;

    @Builder.Default
    private RecurrenceType recurrenceType = RecurrenceType.NONE;
}
