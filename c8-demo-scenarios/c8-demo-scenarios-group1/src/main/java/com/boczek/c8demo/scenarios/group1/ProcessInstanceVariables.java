package com.boczek.c8demo.scenarios.group1;

import com.boczek.c8demo.scenarios.group1.controllers.payloads.StartProcessInstancePayload;

public class ProcessInstanceVariables {

    private StartProcessInstancePayload instancePayload;

    private String content;

    private boolean inputApproved = false;

    public ProcessInstanceVariables() {
    }

    public ProcessInstanceVariables(StartProcessInstancePayload instancePayload) {
        this.instancePayload = instancePayload;
        this.content = instancePayload.content();
    }

    public StartProcessInstancePayload getInstancePayload() {
        return instancePayload;
    }

    public void setInstancePayload(StartProcessInstancePayload instancePayload) {
        this.instancePayload = instancePayload;
    }

    public boolean getInputApproved() {
        return inputApproved;
    }

    public void setInputApproved(boolean inputApproved) {
        this.inputApproved = inputApproved;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
