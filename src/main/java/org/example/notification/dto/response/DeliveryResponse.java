package org.example.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryResponse {
    private boolean success;
    private String message;

    public static DeliveryResponse success() {
        return new DeliveryResponse(true, "Notification delivered successfully");
    }

    public static DeliveryResponse failure(String message) {
        return new DeliveryResponse(
                false,
                message);
    }
}