package com.example.resilience4jdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class Resilience4jDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(Resilience4jDemoApplication.class, args);
    }
} 