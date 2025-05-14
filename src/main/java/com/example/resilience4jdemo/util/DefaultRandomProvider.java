package com.example.resilience4jdemo.util;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class DefaultRandomProvider implements RandomProvider {
    private final Random random = new Random();
    @Override
    public double nextDouble() {
        return random.nextDouble();
    }
} 