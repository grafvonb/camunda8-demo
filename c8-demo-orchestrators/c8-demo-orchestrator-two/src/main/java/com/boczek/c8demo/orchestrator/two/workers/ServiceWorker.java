package com.boczek.c8demo.orchestrator.two.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceWorker extends BaseWorker {

    private static final Logger logger = LoggerFactory.getLogger(ServiceWorker.class);

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        logger.debug(secretInformation);
    }
}
