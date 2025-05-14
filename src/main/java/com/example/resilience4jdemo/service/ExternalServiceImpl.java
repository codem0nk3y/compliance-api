package com.example.resilience4jdemo.service;

import com.example.resilience4jdemo.exception.ServiceException;
import com.example.resilience4jdemo.model.UserData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.resilience4jdemo.util.RandomProvider;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ExternalServiceImpl implements ExternalService {

    private static final String USER_SERVICE = "userService";
    private static final String PROCESS_SERVICE = "processService";
    private static final String ERROR_USER_NOT_FOUND = "USER_NOT_FOUND";
    private static final String ERROR_RATE_LIMIT = "RATE_LIMIT_EXCEEDED";

    private final RandomProvider randomProvider;

    public ExternalServiceImpl(RandomProvider randomProvider) {
        this.randomProvider = randomProvider;
    }

    @Override
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "getUserDataFallback")
    public UserData getUserData(String userId) {
        log.info("Fetching user data for userId: {}", userId);
        
        if ("fail".equals(userId)) {
            throw new ServiceException("External service error", "SERVICE_UNAVAILABLE");
        }
        
        return UserData.builder()
                .userId(userId)
                .username("User " + userId)
                .status("ACTIVE")
                .build();
    }

    public UserData getUserDataFallback(String userId, Throwable t) {
        log.warn("Fallback triggered for userId: {} due to: {}", userId, t.getMessage());
        throw new ServiceException("Service unavailable", "SERVICE_UNAVAILABLE");
    }

    @Override
    @CircuitBreaker(name = PROCESS_SERVICE)
    @RateLimiter(name = PROCESS_SERVICE)
    public String processData(String data) {
        log.info("Processing data: {}", data);
        
        if (randomProvider.nextDouble() < 0.3) {
            throw new ServiceException("Rate limit exceeded", ERROR_RATE_LIMIT);
        }
        
        return "Processed: " + data;
    }
} 
