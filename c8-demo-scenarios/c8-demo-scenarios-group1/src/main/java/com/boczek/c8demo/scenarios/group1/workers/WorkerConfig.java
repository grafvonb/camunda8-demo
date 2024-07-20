package com.boczek.c8demo.scenarios.group1.workers;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

// @Configuration
public class WorkerConfig {

    private static final int WORKER_TIMEOUT = 10;

    private final ZeebeClient client;

    public WorkerConfig(ZeebeClient client) {
        this.client = client;
    }

    @PostConstruct
    public void createWorkers() {
        final JobWorker creditDeductionWorker =
                client.newWorker()
                        .jobType("logMessageWorker")
                        .handler(new LogMessageWorker())
                        .timeout(Duration.ofSeconds(WORKER_TIMEOUT).toMillis())
                        .open();
    }
}
