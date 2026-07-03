package org.example.notification.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SingleNotificationRequest extends BaseNotificationRequest {

    @NotNull
    private String userId;
}