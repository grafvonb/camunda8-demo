package com.boczek.c8demo.orchestrator.two.controllers.responses;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

public class StartProcessInstanceResponse {

    private ProcessInstanceEvent processInstanceEvent;

    private String processBusinessKey;

    public StartProcessInstanceResponse(ProcessInstanceEvent processInstanceEvent, String processBusinessKey) {
        this.processInstanceEvent = processInstanceEvent;
        this.processBusinessKey = processBusinessKey;
    }

    public ProcessInstanceEvent getProcessInstanceEvent() {
        return processInstanceEvent;
    }

    public void setProcessInstanceEvent(ProcessInstanceEvent processInstanceEvent) {
        this.processInstanceEvent = processInstanceEvent;
    }

    public String getProcessBusinessKey() {
        return processBusinessKey;
    }

    public void setProcessBusinessKey(String processBusinessKey) {
        this.processBusinessKey = processBusinessKey;
    }
}
