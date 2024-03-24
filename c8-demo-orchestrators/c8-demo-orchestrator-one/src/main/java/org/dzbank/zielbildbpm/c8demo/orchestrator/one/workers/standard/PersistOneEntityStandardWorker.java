package org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers.standard;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.OneEntity;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.services.OrchestratorOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@SuppressWarnings("unused")
public class PersistOneEntityStandardWorker {

    private static final Logger logger = LoggerFactory.getLogger(PersistOneEntityStandardWorker.class);

    private final OrchestratorOneService oOneService;

    public PersistOneEntityStandardWorker(OrchestratorOneService oOneService) {
        this.oOneService = oOneService;
    }

    @JobWorker(type = "persistOneEntityStandardWorker", autoComplete = false)
    @SuppressWarnings("unused")
    public void persistOneEntity(final JobClient client, final ActivatedJob job) {

        Map<String, Object> variables = job.getVariablesAsMap();

        OneEntity oneEntity = new OneEntity(Long.toString(job.getProcessInstanceKey()), "body one standard");
        OneEntity oneEntityPersisted = oOneService.createOneEntity(oneEntity).block();

        variables.put("oneEntityStandard", oneEntityPersisted);

        client.newCompleteCommand(job.getKey())
                .variables(variables)
                .send() // .join() would block
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });

        logger.debug("PersistOneEntity standard way system task {} completed!", job.getKey());
    }
}