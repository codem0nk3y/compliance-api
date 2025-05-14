package com.example.resilience4jdemo.controller;

import com.example.resilience4jdemo.model.UserData;
import com.example.resilience4jdemo.service.ExternalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExternalService externalService;

    @Test
    void getUserData_ShouldReturnUserData() throws Exception {
        // Given
        String userId = "123";
        UserData userData = new UserData(userId, "User " + userId);
        when(externalService.getUserData(userId)).thenReturn(userData);

        // When/Then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.name").value("User " + userId));
    }

    @Test
    void processData_ShouldReturnProcessedData() throws Exception {
        // Given
        String data = "test data";
        String processedData = "Processed: " + data;
        when(externalService.processData(data)).thenReturn(processedData);

        // When/Then
        mockMvc.perform(post("/api/users/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(content().string(processedData));
    }

    @Test
    void failEndpoint_ShouldReturnServerError() throws Exception {
        // Given
        when(externalService.getUserData("fail"))
            .thenThrow(new RuntimeException("Service error"));

        // When/Then
        mockMvc.perform(get("/api/users/fail"))
                .andExpect(status().isServiceUnavailable());
    }
} 