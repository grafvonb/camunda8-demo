package org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.OneEntity;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.services.OrchestratorOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class LogInformationWorker {

    private static final Logger logger = LoggerFactory.getLogger(LogInformationWorker.class);

    @JobWorker(type = "logInformationWorker")
    @SuppressWarnings("unused")
    public void persistOneEntity(final JobClient client, final ActivatedJob job) {

        ProcessInstanceVariables variables = job.getVariablesAsType(ProcessInstanceVariables.class);

        if (variables.getSendMessageConfig() != null) {
            logger.debug("Logger {} for job {} reached!", variables.getSendMessageConfig().getMessageName(), job.getKey());
        } else {
            logger.debug("Logger reached for job {} but no context available!", job.getKey());
        }
    }
}