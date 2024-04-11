package com.boczek.c8demo.orchestrator.one.model;

import java.util.UUID;
public class OneEntity {

    public OneEntity() {}

    public OneEntity(String correlationId, String bodyOne) {
        this(null, correlationId, bodyOne);
    }

    public OneEntity(UUID id, String correlationId, String bodyOne) {
        this.id = id;
        this.correlationId = correlationId;
        this.bodyOne = bodyOne;
    }

    private UUID id;

    public UUID getId() {
        return id;
    }

    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    private String bodyOne;

    public String getBodyOne() {
        return bodyOne;
    }

    public void setBodyOne(String bodyOne) {
        this.bodyOne = bodyOne;
    }

    @Override
    public String toString() {
        return "OneEntity{" +
                "id=" + id +
                ", correlationId='" + correlationId + '\'' +
                ", bodyOne='" + bodyOne + '\'' +
                '}';
    }
}
