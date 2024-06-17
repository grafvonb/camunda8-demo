package com.boczek.c8demo.scenarios.group1.controllers.payloads;

public record StartProcessInstancePayload(String bpmnProcessId, String content) {
    public StartProcessInstancePayload {
        if (bpmnProcessId == null || bpmnProcessId.isBlank()) {
            throw new IllegalArgumentException("bpmnProcessId must not be null or empty!");
        }
    }
}
