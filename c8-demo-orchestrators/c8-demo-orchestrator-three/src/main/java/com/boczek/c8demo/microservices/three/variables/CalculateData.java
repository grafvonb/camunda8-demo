package com.boczek.c8demo.microservices.three.variables;

public record CalculateData(String operation, Long number1, Long number2) {
    public CalculateData {
        if (operation == null || operation.isBlank()) {
            throw new IllegalArgumentException("Operation must not be null or empty!");
        }
    }
}
