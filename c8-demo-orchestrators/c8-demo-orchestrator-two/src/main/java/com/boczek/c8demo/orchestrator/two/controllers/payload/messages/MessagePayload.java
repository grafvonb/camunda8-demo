package com.boczek.c8demo.orchestrator.two.controllers.payload.messages;

import com.boczek.c8demo.orchestrator.two.configuration.MessageRuntimeConfig;

public abstract class MessagePayload {

    private MessageRuntimeConfig messageRuntimeConfig;

    public MessagePayload() {
        messageRuntimeConfig = new MessageRuntimeConfig();
    }

    public MessageRuntimeConfig getMessageRuntimeConfig() {
        return messageRuntimeConfig;
    }

    public void setMessageRuntimeConfig(MessageRuntimeConfig messageRuntimeConfig) {
        this.messageRuntimeConfig = messageRuntimeConfig;
    }
}
