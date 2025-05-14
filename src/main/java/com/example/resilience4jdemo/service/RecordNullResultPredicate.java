package com.example.resilience4jdemo.service;

import java.util.function.Predicate;

public class RecordNullResultPredicate implements Predicate<Object> {
    @Override
    public boolean test(Object result) {
        return result == null;
    }
} 