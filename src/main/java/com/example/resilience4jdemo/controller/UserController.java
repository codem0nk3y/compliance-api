package com.example.resilience4jdemo.controller;

import com.example.resilience4jdemo.model.UserData;
import com.example.resilience4jdemo.service.ExternalService;
import com.example.resilience4jdemo.exception.ServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Controller", description = "APIs for user data management")
public class UserController {

    private final ExternalService externalService;

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user data by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User data found"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID"),
        @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<UserData> getUserData(
            @Parameter(description = "User ID", required = true)
            @PathVariable @NotBlank String userId) {
        UserData userData = externalService.getUserData(userId);
        if (userData == null) {
            throw new ServiceException("User data not found or service unavailable", "USER_NOT_FOUND");
        }
        return ResponseEntity.ok(userData);
    }

    @PostMapping(value = "/process", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Process user data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Data processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    public ResponseEntity<String> processData(
            @Parameter(description = "Data to process", required = true)
            @RequestBody @Valid @NotBlank String data) {
        return ResponseEntity.ok(externalService.processData(data));
    }

    @GetMapping(value = "/fail", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Test failure scenario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<UserData> failEndpoint() {
        return ResponseEntity.ok(externalService.getUserData("fail"));
    }
} 