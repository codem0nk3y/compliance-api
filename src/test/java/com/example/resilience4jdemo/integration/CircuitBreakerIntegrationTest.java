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
    "resilience4j.circuitbreaker.instances.userService.wait-duration-in-open-state=5s",
    "resilience4j.circuitbreaker.instances.userService.sliding-window-size=10",
    "resilience4j.circuitbreaker.instances.userService.minimum-number-of-calls=5",
    "resilience4j.circuitbreaker.instances.userService.sliding-window-type=count_based",
    "resilience4j.circuitbreaker.instances.userService.automatic-transition-from-open-to-half-open-enabled=true",
    "resilience4j.circuitbreaker.instances.userService.record-exceptions=com.example.resilience4jdemo.exception.ServiceException"
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
    void whenFailedCall_thenCircuitBreakerTriggersFallback() {
        // Given
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());

        // When - Make multiple failed calls
        ResponseEntity<UserData> lastResponse = null;
        for (int i = 0; i < 10; i++) {
            lastResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users/fail",
                UserData.class
            );
        }

        // Then: After enough failures, fallback should be triggered (status 503)
        assertNotNull(lastResponse);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, lastResponse.getStatusCode());
    }

    @Test
    void whenCircuitBreakerOpen_thenCallsReturnServiceUnavailable() {
        // Given: Open the circuit breaker by making enough failed calls
        for (int i = 0; i < 10; i++) {
            ResponseEntity<UserData> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users/fail",
                UserData.class
            );
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        }

        // Then: Circuit breaker should be open
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        // When: Call again, should get fallback (503)
        ResponseEntity<UserData> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/users/fail",
            UserData.class
        );
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }
} 