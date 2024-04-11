package com.boczek.c8demo.orchestrator.one.workers.reactive;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import com.boczek.c8demo.orchestrator.one.model.OneEntity;
import com.boczek.c8demo.orchestrator.one.services.OrchestratorOneService;
import com.boczek.c8demo.orchestrator.one.workers.ProcessInstanceVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@SuppressWarnings("unused")
public class UpdateOneEntityReactiveWorker {

    private static final Logger logger = LoggerFactory.getLogger(UpdateOneEntityReactiveWorker.class);

    private final OrchestratorOneService oOneService;

    public UpdateOneEntityReactiveWorker(OrchestratorOneService oOneService) {
        this.oOneService = oOneService;
    }

    @JobWorker(type = "updateOneEntityReactiveWorker", autoComplete = false)
    public void updateOneEntity(final JobClient client, final ActivatedJob job) {

        ProcessInstanceVariables variables = job.getVariablesAsType(ProcessInstanceVariables.class);

        OneEntity oneEntity = variables.getOneEntity();
        oneEntity.setBodyOne(variables.getCompleteJobConfig().getPayload());

        oOneService.updateOneEntity(oneEntity)
                .subscribe(oneEntityUpdated -> variables.setOneEntity(oneEntityUpdated),
                        error -> {
                            client.newFailCommand(job.getKey())
                                    .retries(job.getRetries() - 1)
                                    .errorMessage("Could not complete job " + job + ((error.getMessage() != null) ? (" due to: " + error.getMessage()) : ""))
                                    .send()
                                    .exceptionally(throwable -> {
                                        throw new RuntimeException("Could not fail job " + job, throwable);
                                    });
                            logger.debug("UpdateOneEntity reactive way system task {} failed (attempts left: {}).", job.getKey(), job.getRetries() - 1);
                        },
                        () -> {
                            UUID messageCorrelationKey = UUID.randomUUID();
                            variables.setMessageCorrelationKey(messageCorrelationKey);
                            client.newCompleteCommand(job.getKey())
                                    .variables(variables)
                                    .send()
                                    .thenApply(jobResponse -> jobResponse)
                                    .exceptionally(throwable -> {
                                        throw new RuntimeException("Could not complete job " + job, throwable);
                                    });
                            logger.debug("UpdateOneEntity reactive way system task {} completed, with message correlation key {}", job.getKey(), messageCorrelationKey);
                        });
    }
}