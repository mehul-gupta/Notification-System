package org.example.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.notification.dto.request.BulkNotificationRequest;
import org.example.notification.dto.response.BulkNotificationResponse;
import org.example.notification.dto.request.SingleNotificationRequest;
import org.example.notification.dto.response.NotificationResponse;
import org.example.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

/**
 * REST APIs for sending single and bulk notifications.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public NotificationResponse send(@Valid @RequestBody SingleNotificationRequest request) {
        return service.send(request);
    }

    @PostMapping("/bulk")
    public BulkNotificationResponse sendBulk(@Valid @RequestBody BulkNotificationRequest request) {
        return service.sendBulk(request);
    }
}