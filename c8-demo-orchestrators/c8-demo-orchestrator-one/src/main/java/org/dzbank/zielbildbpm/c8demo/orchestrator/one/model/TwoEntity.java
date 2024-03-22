package org.dzbank.zielbildbpm.c8demo.orchestrator.one.model;

import java.util.UUID;

public class TwoEntity {

    public TwoEntity() {}

    public TwoEntity(String correlationId, String bodyTwo, UUID oneEntityId) {
        this(null, correlationId, bodyTwo, oneEntityId);
    }

    public TwoEntity(UUID id, String correlationId, String bodyTwo, UUID oneEntityId) {
        this.id = id;
        this.correlationId = correlationId;
        this.bodyTwo = bodyTwo;
        this.oneEntityId = oneEntityId;
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

    private String bodyTwo;

    public String getBodyTwo() {
        return bodyTwo;
    }

    public void setBodyTwo(String bodyTwo) {
        this.bodyTwo = bodyTwo;
    }

    private UUID oneEntityId;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOneEntityId() {
        return oneEntityId;
    }

    public void setOneEntityId(UUID oneEntityId) {
        this.oneEntityId = oneEntityId;
    }

    @Override
    public String toString() {
        return "TwoEntity{" +
                "id=" + id +
                ", correlationId='" + correlationId + '\'' +
                ", bodyTwo='" + bodyTwo + '\'' +
                ", oneEntityId=" + oneEntityId +
                '}';
    }
}
