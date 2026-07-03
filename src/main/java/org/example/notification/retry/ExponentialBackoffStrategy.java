package org.example.notification.retry;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class ExponentialBackoffStrategy implements RetryStrategy{

    @Override
    public long getDelay(int retryCount) {
        return (long) Math.pow(2, retryCount) * 1000;
    }
}