package com.boczek.c8demo.scenarios.group1.workers;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import com.boczek.c8demo.scenarios.group1.services.ShortenContentService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class ShortenContentWorker implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(ShortenContentWorker.class);

    private ShortenContentService shortenContentService;

    public ShortenContentWorker(ShortenContentService shortenContentService) {
        this.shortenContentService = shortenContentService;
    }

    @JobWorker(type = "shortenContentWorker", autoComplete = false)
    public void handle(final JobClient client, final ActivatedJob job)  {
        logger.debug("Running automatic shortening of the content...");

        var variables = job.getVariablesAsType(ProcessInstanceVariables.class);

        variables.setContent(shortenContentService.shortenContent(variables));

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
