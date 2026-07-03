package org.example.notification.model.enums;

public enum NotificationStatus {
    /** Created and waiting to be processed (used for immediate notifications). */
    PENDING,
    /** Scheduled for delivery (used for scheduled notifications) */
    SCHEDULED,
    /** A delivery attempt is in progress. */
    PROCESSING,
    /** Successfully delivered via the target channel. */
    SENT,
    /** All retry attempts exhausted; notification could not be delivered. */
    FAILED,
    /** User or system explicitly cancelled before delivery. */
    CANCELLED
}