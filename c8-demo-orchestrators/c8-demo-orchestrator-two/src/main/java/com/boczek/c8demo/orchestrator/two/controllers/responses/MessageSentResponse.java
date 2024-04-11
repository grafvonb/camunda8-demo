package com.boczek.c8demo.orchestrator.two.controllers.responses;

import io.camunda.zeebe.client.api.response.PublishMessageResponse;

public class MessageSentResponse {

    private PublishMessageResponse publishMessageResponse;

    private String messageName;

    private String processBusinessKey;

    public MessageSentResponse(PublishMessageResponse publishMessageResponse, String messageName, String processBusinessKey) {
        this.publishMessageResponse = publishMessageResponse;
        this.messageName = messageName;
        this.processBusinessKey = processBusinessKey;
    }

    public PublishMessageResponse getPublishMessageResponse() {
        return publishMessageResponse;
    }

    public void setPublishMessageResponse(PublishMessageResponse publishMessageResponse) {
        this.publishMessageResponse = publishMessageResponse;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getProcessBusinessKey() {
        return processBusinessKey;
    }

    public void setProcessBusinessKey(String processBusinessKey) {
        this.processBusinessKey = processBusinessKey;
    }
}
