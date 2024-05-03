package com.boczek.c8demo.orchestrator.one.operator;

import io.camunda.common.exception.SdkException;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.ProcessInstanceState;
import io.camunda.operate.search.ProcessInstanceFilter;
import io.camunda.operate.search.SearchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class OperatorOneService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorOneService.class);

    private CamundaOperateClient operateClient;

    public OperatorOneService(CamundaOperateClient operateClient) {
        this.operateClient = operateClient;
    }

    public Mono<ProcessInstance> retrieveProcessInstance(Long key) {
        logger.debug("Retrieving process instance with a key {}.", key);

        return Mono.fromCallable(() -> operateClient.getProcessInstance(key))
                .onErrorResume(e -> {
                    if (e instanceof SdkException && e.getMessage().contains("Response not successful: 404")) {
                        logger.debug("Process instance with the key {} not found", key);
                        return Mono.empty();
                    } else {
                        return Mono.error(e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<ProcessInstance> retrieveProcessInstances(String bpmnProcessId) {
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
}
