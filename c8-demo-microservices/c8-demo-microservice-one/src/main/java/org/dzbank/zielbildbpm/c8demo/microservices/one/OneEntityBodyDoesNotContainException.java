package org.dzbank.zielbildbpm.c8demo.microservices.one;

public class OneEntityBodyDoesNotContainException extends RuntimeException {

    public OneEntityBodyDoesNotContainException(String message) {
        super("Body has to contain the following text: " + message);
    }
}
