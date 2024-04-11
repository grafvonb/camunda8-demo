package com.boczek.c8demo.microservices.two;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "zielbildbpm_two_entity")
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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    public UUID getId() {
        return id;
    }

    @Column(name = "correlation_id")
    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Column(name = "body_two", length = 20)
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
}
