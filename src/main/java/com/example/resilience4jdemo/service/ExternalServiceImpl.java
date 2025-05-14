package com.example.resilience4jdemo.service;

import com.example.resilience4jdemo.model.UserData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class ExternalServiceImpl implements ExternalService {

    @Autowired
    private Random random;

    @Override
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserDataFallback")
    public UserData getUserData(String userId) {
        log.info("Fetching user data for userId: {}", userId);
        
        if ("fail".equals(userId)) {
            throw new RuntimeException("External service error");
        }
        
        // Simulate external service call
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return new UserData(userId, "User " + userId);
    }

    public UserData getUserDataFallback(String userId, Throwable t) {
        log.warn("Fallback triggered for userId: {} due to: {}", userId, t.toString());
        return null;
    }

    @Override
    @CircuitBreaker(name = "processService")
    public String processData(String data) {
        log.info("Processing data: {}", data);
        
        if (random.nextDouble() < 0.3) {
            throw new RuntimeException("Rate limit exceeded");
        }
        
        return "Processed: " + data;
    }
} 
