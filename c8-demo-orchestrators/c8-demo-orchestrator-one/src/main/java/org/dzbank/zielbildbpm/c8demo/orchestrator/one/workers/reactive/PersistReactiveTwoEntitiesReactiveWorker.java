package org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers.reactive;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.OneEntity;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.TwoEntity;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.services.OrchestratorOneService;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers.ProcessInstanceVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class PersistReactiveTwoEntitiesReactiveWorker implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(PersistReactiveTwoEntitiesReactiveWorker.class);

    private final OrchestratorOneService oOneService;

    public PersistReactiveTwoEntitiesReactiveWorker(OrchestratorOneService oOneService) {
        this.oOneService = oOneService;
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        logger.debug("Trying to persist multiple TwoEntities ");

        ProcessInstanceVariables variables = job.getVariablesAsType(ProcessInstanceVariables.class);
        OneEntity oneEntity = variables.getOneEntity();

        List<TwoEntity> twoEntitiesPersisted = new ArrayList<>();

        oOneService.createRandomNumberOfTwoEntitiesForOneEntityId(oneEntity.getCorrelationId(),
                        "body two camunda", oneEntity.getId(), variables.getRuntimeConfig().isTryUnstable())
                .subscribe(
                        twoEntitiesPersisted::add,
                        error -> {
                            int retries = job.getRetries();
                            if (retries > 1) {
                                logger.warn("Process {} run for job {} into error. Try to retry...", job.getProcessInstanceKey(), job.getKey());
                                client.newFailCommand(job)
                                        .retries(job.getRetries() - 1)
                                        .errorMessage("An error occurred in activity: " + job.getProcessInstanceKey())
                                        .requestTimeout(Duration.ofSeconds(3))
                                        .send()
                                        .join();
                            } else {
                                logger.error("Process {} run into error. No further retries!", job.getProcessInstanceKey());
                                client.newThrowErrorCommand(job)
                                        .errorCode("compensationRequired")
                                        .errorMessage("An error " + error.getMessage() + " occurred in process: " + job.getProcessInstanceKey())
                                        .send()
                                        .join();
                            }
                        },
                        () -> {
                            variables.setTwoEntities(twoEntitiesPersisted);
                            client.newCompleteCommand(job.getKey())
                                    .variables(variables)
                                    .send()
                                    .exceptionally(throwable -> {
                                        throw new RuntimeException("Could not complete job " + job, throwable);
                                    });

                            logger.debug("Process {} persisted {} TwoEntities for OneEntity with the id {}", job.getProcessInstanceKey(), twoEntitiesPersisted.size(), oneEntity.getId());
                        }
                );
    }
}

