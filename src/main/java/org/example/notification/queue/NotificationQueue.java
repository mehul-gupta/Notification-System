package org.example.notification.queue;

import org.example.notification.model.Notification;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * In-memory priority queue used for asynchronous
 * notification processing.
 */
@Component
public class NotificationQueue {

    private final BlockingQueue<Notification> queue = new PriorityBlockingQueue<>();

    public void publish(Notification notification) {
        try {
            queue.put(notification);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while publishing notification", ex);
        }
    }

    public Notification consume() {
        try {
            return queue.take();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}