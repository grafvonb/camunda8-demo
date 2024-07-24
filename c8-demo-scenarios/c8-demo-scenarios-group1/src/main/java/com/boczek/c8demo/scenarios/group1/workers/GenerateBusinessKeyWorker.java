package com.boczek.c8demo.scenarios.group1.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Component
@SuppressWarnings("unused")
public class GenerateBusinessKeyWorker implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(GenerateBusinessKeyWorker.class);

    public static final String VARIABLE_NAME_BUSINESS_KEY = "businessKey";

    @JobWorker(type = "generateBusinessKeyWorker", autoComplete = false, timeout = 100)
    public void handle(final JobClient client, final ActivatedJob job) throws InterruptedException {

        Duration duration = Duration.ofMillis(RandomGenerator.getDefault().nextLong(1000));
        logger.debug("Sleeping for {} seconds...", duration.toMillisPart());
        Thread.sleep(duration);

        UUID businessKey = UUID.randomUUID();
        client.newCompleteCommand(job.getKey())
                .variable(VARIABLE_NAME_BUSINESS_KEY, businessKey)
                .send()
                .exceptionally(cause -> {
                    throw new RuntimeException("Cannot complete the job.");
                });

        logger.debug("Worker done (Process id: {}, Job id: {}). Generated business key: {}", job.getProcessInstanceKey(), job.getKey(), businessKey);
    }
}