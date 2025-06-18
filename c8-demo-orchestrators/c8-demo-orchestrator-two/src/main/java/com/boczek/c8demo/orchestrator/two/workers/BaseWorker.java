package com.boczek.c8demo.orchestrator.two.workers;

import io.camunda.zeebe.client.api.worker.JobHandler;

public abstract class BaseWorker implements JobHandler {

    protected String secretInformation;

    public BaseWorker() {
        this.secretInformation = "secretInformation";
    }
}
