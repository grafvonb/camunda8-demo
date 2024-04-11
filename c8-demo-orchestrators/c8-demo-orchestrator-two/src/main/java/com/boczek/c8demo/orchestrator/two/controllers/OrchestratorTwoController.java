package com.boczek.c8demo.orchestrator.two.controllers;

import com.boczek.c8demo.orchestrator.two.OrchestratorTwoConstants;
import com.boczek.c8demo.orchestrator.two.OrchestratorTwoInstanceVariables;
import com.boczek.c8demo.orchestrator.two.configuration.CompleteJobConfig;
import com.boczek.c8demo.orchestrator.two.controllers.payload.HauptauftragProzessPayload;
import com.boczek.c8demo.orchestrator.two.controllers.payload.messages.*;
import com.boczek.c8demo.orchestrator.two.controllers.responses.MessageSentResponse;
import com.boczek.c8demo.orchestrator.two.controllers.responses.StartProcessInstanceResponse;
import io.camunda.common.exception.SdkException;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.ProcessInstanceState;
import io.camunda.operate.search.ProcessInstanceFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.PublishMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@RestController
@RequestMapping("/process")
public class OrchestratorTwoController {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorTwoController.class);

    private final ZeebeClient client;
    private final CamundaOperateClient operateClient;

    public OrchestratorTwoController(ZeebeClient client, CamundaOperateClient operateClient) {
        this.client = client;
        this.operateClient = operateClient;
    }

    @PostMapping("/start")
    public Mono<StartProcessInstanceResponse> startProcessInstance(@RequestBody HauptauftragProzessPayload payload) {

        OrchestratorTwoInstanceVariables variables = new OrchestratorTwoInstanceVariables(payload);

        ZeebeFuture<ProcessInstanceEvent> future = client.newCreateInstanceCommand()
                .bpmnProcessId(payload.getRuntimeConfig().getBpmnProcessId())
                .latestVersion()
                .variables(variables)
                .send();

        logger.info("Start of the process of the type {} completed!", payload.getRuntimeConfig().getBpmnProcessId());

        return Mono.fromFuture(future::toCompletableFuture)
                .map(processInstanceEvent -> new StartProcessInstanceResponse(processInstanceEvent, variables.getProcessBusinessKey().toString()))
                .timeout(Duration.ofSeconds(30))
                .doOnSuccess(result -> logger.info("Process with id {} started successfully", result.getProcessInstanceEvent().getProcessInstanceKey()))
                .doOnError(error -> logger.error("Error starting process", error));
    }

    @PostMapping("/complete-job")
    public Mono<CompleteJobResponse> completeJob(@RequestBody CompleteJobConfig completeJobConfig) {

        ZeebeFuture<CompleteJobResponse> future = client.newCompleteCommand(completeJobConfig.getJobKey())
                .variable("completeJobConfig", completeJobConfig)
                .send();

        logger.info("Job with the id: {} completed!", completeJobConfig.getJobKey());

        return Mono.fromFuture(future::toCompletableFuture);
    }

    @PostMapping("/neukundenanlage")
    public Mono<MessageSentResponse> createNeukundenanlage(@RequestBody NeukundenanlageMessagePayload messagePayload) {
        messagePayload.getMessageRuntimeConfig().setMessageName(OrchestratorTwoConstants.MESSAGE_NAME_KUNDENANLEGEN);
        return sendMessage(messagePayload);
    }

    @PostMapping("/kontoanlage")
    public Mono<MessageSentResponse> createKontoanlage(@RequestBody KontoanlageMessagePayload messagePayload) {
        messagePayload.getMessageRuntimeConfig().setMessageName(OrchestratorTwoConstants.MESSAGE_NAME_KONTOANLEGEN);
        return sendMessage(messagePayload);
    }

    @PostMapping("/uvzanlage")
    public Mono<MessageSentResponse> createUVZanlage(@RequestBody UVZanlageMessagePayload messagePayload) {
        messagePayload.getMessageRuntimeConfig().setMessageName(OrchestratorTwoConstants.MESSAGE_NAME_UVZANLEGEN);
        return sendMessage(messagePayload);
    }

    @PostMapping("/ausgefuert")
    public Mono<MessageSentResponse> finishProcessInstance(@RequestBody HauptauftragAusgefuertMessagePayload messagePayload) {
        messagePayload.getMessageRuntimeConfig().setMessageName(OrchestratorTwoConstants.MESSAGE_NAME_HAUPTAUFTRAGAUSGEFUERTMESSAGE);
        return sendMessage(messagePayload);
    }

    @GetMapping("{processInstanceKey}")
    public Mono<ProcessInstance> retrieveProcessInstance(@PathVariable("processInstanceKey") Long processInstanceKey) {
        logger.debug("Retrieving process instance with a key {}.", processInstanceKey);

        return Mono.fromCallable(() -> operateClient.getProcessInstance(processInstanceKey))
                .onErrorResume(e -> {
                    if (e instanceof SdkException && e.getMessage().contains("Response not successful: 404")) {
                        logger.debug("Process instance with the key {} not found", processInstanceKey);
                        return Mono.empty();
                    } else {
                        return Mono.error(e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/search")
    public Flux<ProcessInstance> retrieveProcessInstances(@RequestParam String bpmnProcessId) {
        logger.debug("Retrieving process instances of bpmnProcessId: {}", bpmnProcessId);

        ProcessInstanceFilter filter = ProcessInstanceFilter.builder()
                .bpmnProcessId(bpmnProcessId)
                .state(ProcessInstanceState.ACTIVE)
                .build();

        return Flux.defer(() -> {
            try {
                SearchQuery searchQuery = new SearchQuery.Builder()
                        .filter(filter)
                        .build();
                return Flux.fromIterable(operateClient.searchProcessInstances(searchQuery));
            } catch (Exception e) {
                return Flux.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }


    private Mono<MessageSentResponse> sendMessage(MessagePayload messagePayload) {
        ZeebeFuture<PublishMessageResponse> future = client.newPublishMessageCommand()
                .messageName(messagePayload.getMessageRuntimeConfig().getMessageName())
                .correlationKey(messagePayload.getMessageRuntimeConfig().getMessageCorrelationKey())
                .variable(OrchestratorTwoConstants.VARIABLE_NAME_MESSAGE_PAYLOAD, messagePayload)
                .timeToLive(Duration.ofMinutes(1))
                .send();

        logger.info("{} message with body '{}' was sent!", messagePayload.getMessageRuntimeConfig().getMessageName(), messagePayload.getMessageRuntimeConfig().getMessageCorrelationKey());

        return Mono.fromFuture(future::toCompletableFuture)
                .map(publishMessageResponse -> new MessageSentResponse(publishMessageResponse,
                        messagePayload.getMessageRuntimeConfig().getMessageName(),
                        messagePayload.getMessageRuntimeConfig().getMessageCorrelationKey()))
                .doOnSuccess(result -> logger.info("Message with id {} started successfully", messagePayload.getMessageRuntimeConfig().getMessageName()))
                .doOnError(error -> logger.error("Error starting process", error));
    }
}
