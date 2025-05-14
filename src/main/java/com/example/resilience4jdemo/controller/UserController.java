package com.example.resilience4jdemo.controller;

import com.example.resilience4jdemo.model.UserData;
import com.example.resilience4jdemo.service.ExternalService;
import com.example.resilience4jdemo.service.ExternalServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ExternalService externalService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserData> getUserData(@PathVariable String userId) {
        UserData userData = externalService.getUserData(userId);
        if (userData == null) {
            return ResponseEntity.status(503).build();
        }
        return ResponseEntity.ok(userData);
    }

    @PostMapping("/process")
    public ResponseEntity<String> processData(@RequestBody String data) {
        String processedData = externalService.processData(data);
        return ResponseEntity.ok(processedData);
    }

    @GetMapping("/fail")
    public ResponseEntity<UserData> failEndpoint() {
        UserData userData = externalService.getUserData("fail");
        if (userData == null) {
            return ResponseEntity.status(503).build();
        }
        return ResponseEntity.ok(userData);
    }
} 