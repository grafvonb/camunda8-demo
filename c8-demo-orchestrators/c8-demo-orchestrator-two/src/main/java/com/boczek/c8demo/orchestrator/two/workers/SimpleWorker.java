package com.boczek.c8demo.orchestrator.two.workers;


import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class SimpleWorker implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        /* your logic for reading and setting variables here */
    }
}
