package com.example.resilience4jdemo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    @NotBlank(message = "User ID cannot be blank")
    private String userId;
    
    @NotBlank(message = "Username cannot be blank")
    private String username;
    
    // Additional fields can be added here
    private String email;
    private String status;
} 