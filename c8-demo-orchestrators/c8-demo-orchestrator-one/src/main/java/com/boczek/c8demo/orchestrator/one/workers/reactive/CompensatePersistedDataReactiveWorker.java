package com.boczek.c8demo.orchestrator.one.workers.reactive;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import com.boczek.c8demo.orchestrator.one.model.OneEntity;
import com.boczek.c8demo.orchestrator.one.services.OrchestratorOneService;
import com.boczek.c8demo.orchestrator.one.workers.ProcessInstanceVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class CompensatePersistedDataReactiveWorker implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(CompensatePersistedDataReactiveWorker.class);

    private final OrchestratorOneService oOneService;

    public CompensatePersistedDataReactiveWorker(OrchestratorOneService oOneService) {
        this.oOneService = oOneService;
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) {

        ProcessInstanceVariables variables = job.getVariablesAsType(ProcessInstanceVariables.class);
        OneEntity oneEntity = variables.getOneEntity();
        String correlationId = oneEntity.getCorrelationId();

        Flux.merge(
                oOneService.deleteOneEntityByCorrelation(correlationId),
                oOneService.deleteTwoEntitiesByCorrelationId(correlationId)
        ).subscribe(
                result -> {},
                error -> {
                    logger.warn("Process {} run for job {} into error. Try to retry...", job.getProcessInstanceKey(), job.getKey());
                    client.newFailCommand(job)
                            .retries(job.getRetries() - 1)
                            .errorMessage("An error occurred in activity: " + job.getProcessInstanceKey())
                            .requestTimeout(Duration.ofSeconds(3))
                            .send()
                            .join();
                },
                () -> {
                    client.newCompleteCommand(job.getKey())
                            .variables(variables)
                            .send()
                            .exceptionally(throwable -> {
                                throw new RuntimeException("Could not complete job " + job, throwable);
                            });
                    logger.debug("Process {} reactively deleted OneEntity and all TwoEntities correlated by id {}", job.getProcessInstanceKey(), oneEntity.getCorrelationId());
                }
        );
    }
}
