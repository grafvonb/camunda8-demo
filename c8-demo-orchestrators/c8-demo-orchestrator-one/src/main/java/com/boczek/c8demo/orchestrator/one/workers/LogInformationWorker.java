package com.boczek.c8demo.orchestrator.one.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import com.boczek.c8demo.orchestrator.one.model.OneEntity;
import com.boczek.c8demo.orchestrator.one.services.OrchestratorOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class LogInformationWorker {

    private static final Logger logger = LoggerFactory.getLogger(LogInformationWorker.class);

    @JobWorker(type = "logInformationWorker")
    public void logInformationWorker(final JobClient client, final ActivatedJob job) {

        ProcessInstanceVariables variables = job.getVariablesAsType(ProcessInstanceVariables.class);

        if (variables.getSendMessageConfig() != null) {
            logger.debug("Logger {} for business correlation {} for job {} reached!", variables.getSendMessageConfig().getMessageName(), variables.getBusinessCorrelationId(), job.getKey());
        } else {
            logger.debug("Logger {} for business correlation {} reached for job {} but no context available!", job.getVariablesAsMap().get("loggerName"), variables.getBusinessCorrelationId(), job.getKey());
        }
    }
}