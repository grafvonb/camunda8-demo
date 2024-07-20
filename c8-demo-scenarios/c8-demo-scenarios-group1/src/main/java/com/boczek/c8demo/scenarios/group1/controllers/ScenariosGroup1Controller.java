package com.boczek.c8demo.scenarios.group1.controllers;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import com.boczek.c8demo.scenarios.group1.controllers.payloads.StartProcessInstancePayload;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/process")
public class ScenariosGroup1Controller {

    private static final Logger logger = LoggerFactory.getLogger(ScenariosGroup1Controller.class);

    private final ZeebeClient client;

    public ScenariosGroup1Controller(ZeebeClient client) {
        this.client = client;
    }

    @PostMapping("/start-reactive")
    // @PreAuthorize("hasRole('process-start-role')")
    public Mono<ProcessInstanceEvent> startProcessInstanceReactive(@RequestBody StartProcessInstancePayload payload) {
        logger.debug("Starting process instance {} in a reactive way...", payload.bpmnProcessId());

        var variables = new ProcessInstanceVariables(payload);

        var future = client.newCreateInstanceCommand()
                .bpmnProcessId(payload.bpmnProcessId())
                .latestVersion()
                .variables(variables)
                .send();

        return Mono.fromFuture(future::toCompletableFuture)
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(result -> logger.info("Process with id {} started successfully", result.getProcessInstanceKey()))
                .doOnError(error -> logger.error("Error starting process", error));
    }

    @PostMapping ("/start-blocking")
    public ProcessInstanceEvent startProcessInstanceBlocking(@RequestBody StartProcessInstancePayload payload) {
        logger.debug("Starting process instance {} in a blocking way...", payload.bpmnProcessId());

        var variables = new ProcessInstanceVariables(payload);

        var result = client.newCreateInstanceCommand()
                .bpmnProcessId(payload.bpmnProcessId())
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        return result;
    }
 }
