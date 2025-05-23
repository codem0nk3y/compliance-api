package com.example.resilience4jdemo.service;

import com.example.resilience4jdemo.model.UserData;
import com.example.resilience4jdemo.util.RandomProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalServiceImplTest {

    @Mock
    private RandomProvider randomProvider;

    @InjectMocks
    private ExternalServiceImpl externalService;

    @BeforeEach
    void setUp() {
        // Reset mocks
        reset(randomProvider);
    }

    @Test
    void getUserData_ShouldReturnUserData() {
        // Given
        String userId = "123";

        // When
        UserData result = externalService.getUserData(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("User " + userId, result.getUsername());
    }

    @Test
    void getUserData_ShouldThrowExceptionForFailUserId() {
        // Given
        String userId = "fail";

        // When & Then
        assertThrows(RuntimeException.class, () -> externalService.getUserData(userId));
    }

    @Test
    void processData_ShouldReturnProcessedData() {
        // Given
        String data = "test data";
        when(randomProvider.nextDouble()).thenReturn(0.4); // Above threshold

        // When
        String result = externalService.processData(data);

        // Then
        assertEquals("Processed: " + data, result);
        verify(randomProvider).nextDouble();
    }

    @Test
    void processData_ShouldThrowExceptionOnRateLimit() {
        // Given
        String data = "test data";
        when(randomProvider.nextDouble()).thenReturn(0.2); // Below threshold

        // When & Then
        assertThrows(RuntimeException.class, () -> externalService.processData(data));
        verify(randomProvider).nextDouble();
    }
} 