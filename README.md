# Notification System

A scalable, extensible notification system built with Spring Boot that supports multiple notification channels (Email, SMS, Push, WhatsApp, Slack) with retry mechanisms, scheduling, and priority-based queuing.

## Overview

This notification system is designed to handle various types of notifications through multiple channels with features like:
- Multiple notification channels (Email, SMS, Push, WhatsApp, Slack)
- Priority-based processing
- Retry mechanisms with exponential backoff
- Scheduled notifications
- Recurring notifications
- Bulk notification processing
- Asynchronous processing with threading

## Architecture

The system follows a modular, layered architecture:

```
notification-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/notification/
│   │   │       ├── controller/          # REST controllers
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       ├── exceptions/          # Custom exceptions
│   │   │       ├── model/               # Data models and enums
│   │   │       ├── queue/               # Queuing mechanism
│   │   │       ├── retry/               # Retry mechanisms
│   │   │       ├── scheduler/           # Scheduling components
│   │   │       ├── service/             # Business logic
│   │   │       │   └── impl/            # Service implementations
│   │   │       ├── worker/              # Worker threads for processing
│   │   │       └── config/              # Configuration classes
│   │   └── resources/
│   │       └── application.yml          # Application configuration
│   └── test/                            # Tests (not shown in snippet)
└── pom.xml                              # Maven dependencies
```

## Key Components

### Controllers
- `NotificationController`: Handles incoming notification requests via REST API
  - `POST /api/v1/notifications` - Send single notification
  - `POST /api/v1/notifications/bulk` - Send bulk notifications

### Services
- `NotificationService`: Interface defining notification operations
- `NotificationServiceImpl`: Implementation handling validation, scheduling, and publishing

### Models
- `Notification`: Core notification entity with fields like ID, user ID, title, message, channel, priority, status, etc.
- `User`: User entity with contact information and preferred channels
- Enums: `NotificationChannel`, `NotificationPriority`, `NotificationStatus`, `RecurrenceType`

### Queue System
- `NotificationQueue`: Thread-safe priority queue using `PriorityBlockingQueue`
- `NotificationPublisher`: Publishes notifications to the queue
- `NotificationConsumer`: Continuously consumes notifications from the queue and delegates to workers

### Processing Components
- `NotificationWorker`: Processes notifications by routing to appropriate channel handlers
- `NotificationScheduler`: Periodically checks for scheduled notifications and moves them to pending state
- `RetryProcessor`: Handles failed notifications with exponential backoff retry mechanism
- `ExponentialBackoffStrategy`: Implements retry delay strategy (2^n * 1000ms)

### Channel Handlers
- `NotificationChannelHandler`: Interface for channel-specific implementations
- Concrete implementations:
  - `EmailChannelHandler`
  - `SmsChannelHandler`
  - `PushChannelHandler`
  - (WhatsApp and Slack handlers would follow similar pattern)
- `ChannelFactory`: Factory pattern for resolving appropriate channel handler

### Configuration
- Thread pool configuration for consumer workers
- Notification-specific settings (retry policies, queue settings, channel toggles)

## Features

### 1. Multiple Notification Channels
Supports Email, SMS, Push, WhatsApp, and Slack channels. Each channel has a dedicated handler that implements the `NotificationChannelHandler` interface.

### 2. Priority-Based Processing
Notifications are processed based on priority (HIGH > MEDIUM > LOW) using a priority queue. Higher priority notifications are processed first.

### 3. Scheduling & Recurring Notifications
- **Scheduled Notifications**: Set `scheduledAt` for future delivery
- **Recurring Notifications**: Set `recurrenceType` for repeating notifications (MINUTELY, HOURLY, DAILY, WEEKLY, MONTHLY)

### 4. Retry Mechanism
Failed notifications are automatically retried with exponential backoff (configurable). Default: 3 attempts with delays of 1s, 2s, 4s...

### 5. Bulk Operations
Efficiently send notifications to multiple users in a single request.

### 6. Asynchronous Processing
Uses thread pools for non-blocking notification processing, ensuring high throughput.

### 7. Error Handling
Global exception handling for common scenarios like invalid users or channels.

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+

### Building the Project
```bash
mvn clean package
```

### Running the Application
```bash
mvn spring-boot:run
```

The application will start on port 8080 (configurable in application.yml).

## API Endpoints

### Send Single Notification
```http
POST /api/v1/notifications
Content-Type: application/json

{
  "title": "Welcome!",
  "message": "Welcome to our service!",
  "userId": "user123",
  "channel": "EMAIL",
  "priority": "HIGH",
  "scheduledAt": "2023-12-01T10:00:00",
  "recurrenceType": "NONE"
}
```

### Send Bulk Notifications
```http
POST /api/v1/notifications/bulk
Content-Type: application/json

{
  "title": "Promotion",
  "message": "Special offer today!",
  "userIds": ["user1", "user2", "user3"],
  "channel": "SMS",
  "priority": "MEDIUM"
}
```

### Response Format
```json
{
  "notificationId": "uuid-string",
  "status": "PENDING|SENT|FAILED|SCHEDULED|PROCESSING|CANCELLED"
}
```

For bulk requests:
```json
{
  "total": 3,
  "success": 2,
  "failed": 1
}
```

## Configuration

Edit `src/main/resources/application.yml` to configure:

```yaml
server:
  port: 8080

notification:
  retry:
    max-attempts: 3
    initial-interval-ms: 1000
    multiplier: 2.0
  
  queue:
    processor-threads: 4
    batch-size: 100

  channels:
    email:
      enabled: true
      mock: true
    sms:
      enabled: true
      mock: true
    push:
      enabled: true
      mock: true
```

Set `mock: false` to enable actual channel implementations (would require integration with external services).

## How It Works

1. **Request Handling**: REST controllers receive notification requests and validate them
2. **Validation**: Service validates user existence and channel preferences
3. **Notification Creation**: Notification object is created with appropriate status (PENDING or SCHEDULED)
4. **Persisting**: Notification is saved to repository
5. **Queuing**: If status is PENDING, notification is published to the queue
6. **Processing**: Consumer threads pick up notifications and delegate to workers
7. **Channel Routing**: Worker uses factory to get appropriate channel handler for the notification's channel
8. **Delivery**: Handler attempts delivery (currently mocked in this implementation)
9. **Retry Logic**: Failed deliveries are retried with exponential backoff
10. **Recurring**: For recurring notifications, next occurrence is scheduled upon successful delivery

## Design Patterns Used

- **Factory Pattern**: ChannelFactory for resolving channel handlers
- **Strategy Pattern**: RetryStrategy for different backoff algorithms
- **Observer Pattern**: Event-driven processing via queue
- **Singleton Pattern**: Spring-managed beans for services and components
- **Template Method**: Common processing flow in NotificationWorker with hook methods

## Extending the System

### Adding a New Notification Channel
1. Create a new class implementing `NotificationChannelHandler`
2. Add the channel to `NotificationChannel` enum
3. Implement the `send()` method with your channel-specific logic
4. Register the component with `@Component` (Spring will auto-register it with ChannelFactory)
5. Add configuration in `application.yml` under `notification.channels`

## Testing

The project includes unit tests for core components (not shown in the provided code snippet but would typically be in `src/test/java`).

## Performance Considerations

- **Thread Pool Configuration**: Adjust `processor-threads` in application.yml based on workload
- **Batch Processing**: Configure `batch-size` for optimal throughput
- **Priority Queuing**: Ensures important notifications are processed first
- **Non-blocking I/O**: Asynchronous processing prevents thread blocking

## Limitations & Future Improvements

### Current Limitations
- Channel implementations are mocked (logging only)
- Uses in-memory queue (not persistent across restarts)
- No persistence layer shown in code snippets (would typically use a database)

### Potential Enhancements
1. Replace in-memory queue with a robust message queue (RabbitMQ, Apache Kafka, etc.)
2. Add persistent storage for notifications and users
3. Implement actual channel integrations (SMTP clients, SMS gateways, push services)
4. Add monitoring and metrics collection
5. Implement rate limiting per channel
6. Add dashboard for monitoring notification status and metrics
7. Implement message templates and templating engine

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or support, please open an issue in the repository.