package com.boczek.c8demo.orchestrator.two.configuration;

public class RuntimeConfig {

    private String bpmnProcessId;

    private boolean debugSkipCLMK = true;

    public String getBpmnProcessId() {
        return bpmnProcessId;
    }

    public void setBpmnProcessId(String bpmnProcessId) {
        this.bpmnProcessId = bpmnProcessId;
    }

    public boolean isDebugSkipCLMK() {
        return debugSkipCLMK;
    }

    public void setDebugSkipCLMK(boolean debugSkipCLMK) {
        this.debugSkipCLMK = debugSkipCLMK;
    }
}