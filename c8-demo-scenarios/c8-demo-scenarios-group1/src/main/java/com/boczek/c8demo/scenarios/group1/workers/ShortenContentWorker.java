package com.boczek.c8demo.scenarios.group1.workers;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ShortenContentWorker {

    private static final Logger logger = LoggerFactory.getLogger(ShortenContentWorker.class);

    @JobWorker(type = "shortenContentWorker", autoComplete = false)
    public void handle(final JobClient client, final ActivatedJob job)  {
        logger.debug("Running automatic shortening of the content...");

        var variables = job.getVariablesAsType(ProcessInstanceVariables.class);

        var content = variables.getContent();

        if (content != null && content.length() > ValidateInputWorker.VALIDATE_INPUT_MAX_CHARS) {
            variables.setContent(content.substring(0, ValidateInputWorker.VALIDATE_INPUT_MAX_CHARS));
        }

        variables.setInputApproved(false);
        client.newCompleteCommand(job.getKey())
                .variables(variables)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });

        logger.debug("Content shortening completed.");
    }
}
