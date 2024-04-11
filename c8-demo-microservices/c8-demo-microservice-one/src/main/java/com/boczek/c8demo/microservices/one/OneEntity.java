package com.boczek.c8demo.microservices.one;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "zielbildbpm_one_entity")
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

    @Column(name = "body_one", length = 20)
    private String bodyOne;

    public String getBodyOne() {
        return bodyOne;
    }

    public void setBodyOne(String bodyOne) {
        this.bodyOne = bodyOne;
    }
}
