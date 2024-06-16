package com.boczek.c8demo.scenarios.group1.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class LogMessageWorker {

    private static final Logger logger = LoggerFactory.getLogger(LogMessageWorker.class);

    public static final String VARIABLE_NAME_LOG_MESSAGE = "logMessage";

    @JobWorker(type = "logMessageWorker")
    public void logInformationWorker(final JobClient client, final ActivatedJob job) {

        String message = (String) job.getVariable(VARIABLE_NAME_LOG_MESSAGE);
        if (message == null || message.isBlank()) {
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("logMessage variable must not be null or empty!")
                    .send();
        }

        logger.debug("{} (Process id: {}, Job id: {})", message, job.getProcessInstanceKey(), job.getKey());
    }
}