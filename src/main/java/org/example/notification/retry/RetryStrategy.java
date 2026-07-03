package org.example.notification.retry;

public interface RetryStrategy {
    long getDelay(int retryCount);
}