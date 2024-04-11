package com.boczek.c8demo.orchestrator.one.workers.reactive;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import com.boczek.c8demo.orchestrator.one.OrchestrationOneConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RunTaskInParallelWorker implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTwoEntitiesReactiveWorker.class);

    private final ZeebeClient zeebeClient;

    public RunTaskInParallelWorker(ZeebeClient client) {
        this.zeebeClient = client;
    }

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        logger.debug("Sending message to run a user task in parallel...");

        if (job.getVariable(OrchestrationOneConstants.VARIABLE_NAME_BUSINESS_CORRELATION_ID) instanceof String businessCorrelationId) {

            String messageName;
            switch (job.getElementId()) {
                case "RunTaskInParallelThrowEvent":
                    messageName = OrchestrationOneConstants.MESSAGE_NAME_RUN_TASK_IN_PARALLEL;
                    break;
                case "RunTaskInParallelEndEvent":
                    messageName = OrchestrationOneConstants.MESSAGE_NAME_RUN_TASK_IN_PARALLEL_COMPLETED;
                    break;
                default:
                    throw new RuntimeException("Unknown message source: " + job.getElementId());
            }

            zeebeClient.newPublishMessageCommand()
                    .messageName(messageName)
                    .correlationKey(businessCorrelationId)
                    .send();

            client.newCompleteCommand(job.getKey())
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job " + job, throwable);
                    });

            logger.debug("Sending message to run a user task in parallel completed.");
        } else {
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("No business correlation id provided!")
                    .send()
                    .join();
        }
    }
}
