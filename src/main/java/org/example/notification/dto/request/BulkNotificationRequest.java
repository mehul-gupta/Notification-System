package org.example.notification.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BulkNotificationRequest extends BaseNotificationRequest {

    @NotEmpty
    private List<String> userIds;

    public SingleNotificationRequest toNotificationRequest(
            String userId) {

        return SingleNotificationRequest.builder()
                .userId(userId)
                .title(getTitle())
                .message(getMessage())
                .channel(getChannel())
                .priority(getPriority())
                .scheduledAt(getScheduledAt())
                .recurrenceType(getRecurrenceType())
                .build();
    }
}