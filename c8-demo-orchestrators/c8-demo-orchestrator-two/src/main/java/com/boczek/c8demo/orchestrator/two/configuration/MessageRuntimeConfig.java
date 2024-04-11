package com.boczek.c8demo.orchestrator.two.configuration;

public class MessageRuntimeConfig {

    private String messageName;

    private String messageCorrelationKey;

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageCorrelationKey() {
        return messageCorrelationKey;
    }

    public void setMessageCorrelationKey(String messageCorrelationKey) {
        this.messageCorrelationKey = messageCorrelationKey;
    }
}
