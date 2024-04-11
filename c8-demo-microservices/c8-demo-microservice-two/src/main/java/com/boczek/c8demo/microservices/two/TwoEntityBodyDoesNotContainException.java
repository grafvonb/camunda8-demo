package com.boczek.c8demo.microservices.two;

public class TwoEntityBodyDoesNotContainException extends RuntimeException {

    public TwoEntityBodyDoesNotContainException(String message) {
        super("Body has to contain the following text: " + message);
    }
}
