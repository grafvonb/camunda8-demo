package org.dzbank.zielbildbpm.c8demo.orchestrator.one.controllers;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
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
    public Mono<ProcessInstanceEvent> startProcessInstance(@RequestBody String processKey) {
        ZeebeFuture<ProcessInstanceEvent> future = client.newCreateInstanceCommand()
                .bpmnProcessId(processKey)
                .latestVersion()
                .send();

        logger.info("Start of the process with the key {} completed!", processKey);

        return Mono.fromFuture(future::toCompletableFuture)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(result -> logger.info("Process with id {} started successfully", result.getProcessInstanceKey()))
                .doOnError(error -> logger.error("Error starting process", error));
    }

    @PostMapping("complete-job")
    public Mono<CompleteJobResponse> releaseActionManually(@RequestBody long jobKey) {
        ZeebeFuture<CompleteJobResponse> future = client.newCompleteCommand(jobKey)
                .send();

        logger.info("Job with the id: {} completed!", jobKey);

        return Mono.fromFuture(future::toCompletableFuture);
    }
}
