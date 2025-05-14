# Spring Boot Resilience4j Demo

A demonstration project showcasing the implementation of Resilience4j patterns in a Spring Boot application. This project includes examples of Circuit Breaker, Retry, and Rate Limiter patterns.

## Features

- Circuit Breaker implementation for fault tolerance
- Retry mechanism with exponential backoff
- Rate Limiter for controlling request rates
- Spring Boot Actuator integration for monitoring
- Prometheus metrics export

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Spring Boot 3.x

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/resilience4j-demo.git
cd resilience4j-demo
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on port 8080.

## Configuration

The application uses both `application.properties` and `application.yml` for configuration. Key configurations include:

### Circuit Breaker
- Failure rate threshold: 50%
- Wait duration in open state: 1s
- Sliding window size: 3
- Minimum number of calls: 3

### Rate Limiter
- Limit for period: 10 requests
- Limit refresh period: 1s
- Timeout duration: 0s

## Monitoring

The application exposes several actuator endpoints for monitoring:

- `/actuator/health` - Health check endpoint
- `/actuator/metrics` - Application metrics
- `/actuator/circuitbreakers` - Circuit breaker status
- `/actuator/circuitbreakerevents` - Circuit breaker events
- `/actuator/retries` - Retry status
- `/actuator/retryevents` - Retry events
- `/actuator/ratelimiters` - Rate limiter status
- `/actuator/ratelimiterevents` - Rate limiter events

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 