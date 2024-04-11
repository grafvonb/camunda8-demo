package com.boczek.c8demo.orchestrator.one.workers.reactive;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import com.boczek.c8demo.orchestrator.one.services.OrchestratorOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UpdateTwoEntitiesReactiveWorker implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTwoEntitiesReactiveWorker.class);

    private final OrchestratorOneService oOneService;

    public UpdateTwoEntitiesReactiveWorker(OrchestratorOneService oOneService) {
        this.oOneService = oOneService;
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) {

        Map<String, Object> variables = job.getVariablesAsMap();
        logger.debug("Fake update of the TwoEntity: {}", variables.get("twoEntityForOutput"));

        client.newCompleteCommand(job.getKey())
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });
    }
}
