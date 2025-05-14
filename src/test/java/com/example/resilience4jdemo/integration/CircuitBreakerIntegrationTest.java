package com.example.resilience4jdemo.integration;

import com.example.resilience4jdemo.model.UserData;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
    "resilience4j.circuitbreaker.instances.userService.failure-rate-threshold=50",
    "resilience4j.circuitbreaker.instances.userService.wait-duration-in-open-state=1s",
    "resilience4j.circuitbreaker.instances.userService.sliding-window-size=4",
    "resilience4j.circuitbreaker.instances.userService.minimum-number-of-calls=4",
    "resilience4j.circuitbreaker.instances.userService.sliding-window-type=count_based",
    "resilience4j.circuitbreaker.instances.userService.automatic-transition-from-open-to-half-open-enabled=true",
    "resilience4j.circuitbreaker.instances.userService.record-exceptions=java.lang.RuntimeException"
})
class CircuitBreakerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("userService");
        circuitBreaker.reset();
    }

    @Test
    void whenSuccessfulCall_thenCircuitBreakerStaysClosed() {
        // Given
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());

        // When
        ResponseEntity<UserData> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/users/123",
            UserData.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    @Test
    void whenFailedCall_thenCircuitBreakerOpens() {
        // Given
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());

        // When - Make multiple failed calls
        for (int i = 0; i < 4; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users/fail",
                String.class
            );
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        }

        // Then
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }

    @Test
    void whenCircuitBreakerOpen_thenCallsReturnServiceUnavailable() {
        // Given
        // Make circuit breaker open
        for (int i = 0; i < 4; i++) {
            restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users/fail",
                String.class
            );
        }
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/users/fail",
            String.class
        );

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }
} 