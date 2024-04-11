package com.boczek.c8demo.orchestrator.two.workers;

import com.boczek.c8demo.orchestrator.two.OrchestratorTwoConstants;
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

    @JobWorker(type = "logMessageWorker")
    public void logInformationWorker(final JobClient client, final ActivatedJob job) {

        String message = (String) job.getVariable(OrchestratorTwoConstants.VARIABLE_NAME_LOG_MESSAGE);

        logger.debug("{} (Process id: {}, Job id: {})", message, job.getProcessInstanceKey(), job.getKey());
    }
}