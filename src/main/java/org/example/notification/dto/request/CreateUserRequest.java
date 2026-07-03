package org.example.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.example.notification.model.enums.NotificationChannel;
import java.util.Set;

@Data
public class CreateUserRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    private String deviceToken;

    @NotEmpty
    private Set<NotificationChannel> preferredChannels;
}