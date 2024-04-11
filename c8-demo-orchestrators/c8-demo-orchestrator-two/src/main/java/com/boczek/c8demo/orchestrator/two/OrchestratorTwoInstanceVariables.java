package com.boczek.c8demo.orchestrator.two;

import com.boczek.c8demo.orchestrator.two.controllers.payload.HauptauftragProzessPayload;

import java.util.UUID;

public class OrchestratorTwoInstanceVariables {

    private UUID processBusinessKey;

    private HauptauftragProzessPayload payload;

    public OrchestratorTwoInstanceVariables(HauptauftragProzessPayload payload) {
        this();
        this.payload = payload;
    }

    public OrchestratorTwoInstanceVariables() {
        processBusinessKey = UUID.randomUUID();
    }

    public UUID getProcessBusinessKey() {
        return processBusinessKey;
    }

    public void setProcessBusinessKey(UUID processBusinessKey) {
        this.processBusinessKey = processBusinessKey;
    }

    public HauptauftragProzessPayload getPayload() {
        return payload;
    }

    public void setPayload(HauptauftragProzessPayload payload) {
        this.payload = payload;
    }
}
