package com.boczek.c8demo.microservices.three.workers;

import com.boczek.c8demo.microservices.three.variables.ProcessInstanceVariables;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CalculateWorker {

    private static final Logger logger = LoggerFactory.getLogger(CalculateWorker.class);

    @JobWorker(type = "calculate")
    public void calculateValueWorker(final JobClient client, final ActivatedJob job) {

        var variables = job.getVariablesAsType(ProcessInstanceVariables.class);

        try {
            Long result = switch (variables.getData().operation()) {
                case "add" -> variables.getData().number1() + variables.getData().number2();
                case "sub" -> variables.getData().number1() - variables.getData().number2();
                case "mul" -> variables.getData().number1() * variables.getData().number2();
                case "div" -> variables.getData().number1() / variables.getData().number2();
                default -> throw new IllegalArgumentException("Invalid operation: %s".formatted(variables.getData().operation()));
            };
            variables.setResult(result);

            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .thenApply(jobResponse -> jobResponse)
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job " + job, throwable);
                    });
            logger.debug("Calculate worker completed for system task {}.", job.getKey());

        } catch (Exception error) {
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Could not complete job " + job + ((error.getMessage() != null) ? (" due to: " + error.getMessage()) : ""))
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not fail job " + job, throwable);
                    });
            logger.debug("PersistOneEntity reactive way system task {} failed (attempts left: {}).", job.getKey(), job.getRetries() - 1);
        }

    }
}