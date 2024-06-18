package com.boczek.c8demo.scenarios.group1;

import com.boczek.c8demo.scenarios.group1.controllers.payloads.StartProcessInstancePayload;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcessInstanceVariables that = (ProcessInstanceVariables) o;
        return inputApproved == that.inputApproved && Objects.equals(instancePayload, that.instancePayload) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instancePayload, content, inputApproved);
    }

    @Override
    public String toString() {
        return "{content = " + content + ", inputApproved = " + inputApproved + "}";
    }
}
