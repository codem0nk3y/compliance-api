package com.example.resilience4jdemo.service;

import com.example.resilience4jdemo.model.UserData;

public interface ExternalService {
    UserData getUserData(String userId);
    String processData(String data);
} 