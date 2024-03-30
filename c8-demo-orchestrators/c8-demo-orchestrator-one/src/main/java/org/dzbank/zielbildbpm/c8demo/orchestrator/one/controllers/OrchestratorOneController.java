package org.dzbank.zielbildbpm.c8demo.orchestrator.one.controllers;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration.CompleteJobConfig;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration.RuntimeConfig;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration.SendMessageConfig;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers.ProcessInstanceVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/process")
public class OrchestratorOneController {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorOneController.class);

    private final ZeebeClient client;

    public OrchestratorOneController(ZeebeClient client) {
        this.client = client;
    }

    @PostMapping("/start")
    public Mono<ProcessInstanceEvent> startProcessInstance(@RequestBody RuntimeConfig runtimeConfig) {

        ProcessInstanceVariables variables = new ProcessInstanceVariables(runtimeConfig);

        ZeebeFuture<ProcessInstanceEvent> future = client.newCreateInstanceCommand()
                .bpmnProcessId(runtimeConfig.getWorkflowType())
                .latestVersion()
                .variables(variables)
                .send();

        logger.info("Start of the process of the type {} completed!", runtimeConfig.getWorkflowType());

        return Mono.fromFuture(future::toCompletableFuture)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(result -> logger.info("Process with id {} started successfully", result.getProcessInstanceKey()))
                .doOnError(error -> logger.error("Error starting process", error));
    }

    @PostMapping("complete-job")
    public Mono<CompleteJobResponse> completeJob(@RequestBody CompleteJobConfig completeJobConfig) {

        ZeebeFuture<CompleteJobResponse> future = client.newCompleteCommand(completeJobConfig.getJobKey())
                .variable("completeJobConfig", completeJobConfig)
                .send();

        logger.info("Job with the id: {} completed!", completeJobConfig.getJobKey());

        return Mono.fromFuture(future::toCompletableFuture);
    }

    @PostMapping("send-message")
    public Mono<PublishMessageResponse> sendMessage(@RequestBody SendMessageConfig sendMessageConfig) {

        ZeebeFuture<PublishMessageResponse> future = client.newPublishMessageCommand()
                .messageName(sendMessageConfig.getMessageName())
                .correlationKey(sendMessageConfig.getMessageCorrelationKey())
                .send();

        logger.info("{} message with body '{}' was sent!", sendMessageConfig.getMessageName(), sendMessageConfig.getMessageBody());

        return Mono.fromFuture(future::toCompletableFuture);
    }
}
