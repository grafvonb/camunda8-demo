package com.boczek.c8demo.orchestrator.one.controllers;

import io.camunda.operate.model.ProcessInstance;
import com.boczek.c8demo.orchestrator.one.operator.OperatorOneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/operator")
public class OperatorOneController {

    private static final Logger logger = LoggerFactory.getLogger(OperatorOneController.class);

    private OperatorOneService service;

    public OperatorOneController(OperatorOneService service) {
        this.service = service;
    }

    @GetMapping("process-instances/{key}")
    public Mono<ProcessInstance> retrieveProcessInstance(@PathVariable Long key) {
         return service.retrieveProcessInstance(key)
                 .onErrorResume(e -> {
                     logger.error("Retrieving process instance {} causes error", key, e);
                     return Mono.error(e);
                 })
                 .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT, "Process instance not found for the key " + key)));
    }

    @GetMapping("process-instances")
    public Flux<ProcessInstance> retrieveProcessInstances(@RequestParam String bpmnProcessId) {
        return service.retrieveProcessInstances(bpmnProcessId)
                .onErrorResume(e -> {
                    logger.error("Retrieving process instances für id {} causes error", bpmnProcessId, e);
                    return Flux.error(e);
                })
                .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NO_CONTENT, "No active process instance found for the process id " +  bpmnProcessId)));
    }
}
