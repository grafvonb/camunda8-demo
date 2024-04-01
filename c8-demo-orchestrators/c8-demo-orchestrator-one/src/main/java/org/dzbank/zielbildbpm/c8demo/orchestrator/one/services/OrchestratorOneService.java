package org.dzbank.zielbildbpm.c8demo.orchestrator.one.services;

import org.dzbank.zielbildbpm.c8demo.orchestrator.one.OrchestrationOneConstants;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.OneEntity;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.TwoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

@Service
public class OrchestratorOneService {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorOneService.class);

    private final WebClient msOneWebClient;
    private final WebClient msTwoWebClient;

    public OrchestratorOneService(WebClient.Builder webClientBuilder,
                                  @Value("${microservice-one.rest.base-url}") String msOneBaseUrl,
                                  @Value("${microservice-two.rest.base-url}") String msTwoBaseUrl) {
        this.msOneWebClient = webClientBuilder.baseUrl(msOneBaseUrl).build();
        this.msTwoWebClient = webClientBuilder.baseUrl(msTwoBaseUrl).build();
    }

    public Mono<OneEntity> createOneEntity(OneEntity entity) {
        return msOneWebClient.post()
                .uri("/entities")
                .body(Mono.just(entity), OneEntity.class)
                .retrieve()
                .bodyToMono(OneEntity.class);
    }

    public Mono<OneEntity> updateOneEntity(OneEntity entity) {
        return msOneWebClient.put()
                .uri("/entities/{id}", entity.getId())
                .body(Mono.just(entity), OneEntity.class)
                .retrieve()
                .bodyToMono(OneEntity.class)
                .switchIfEmpty(Mono.empty());
    }

    public Flux<OneEntity> retrieveAllOneEntities() {
        return msOneWebClient.get()
                .uri("/entities")
                .retrieve()
                .bodyToFlux(OneEntity.class);
    }

    public Mono<Void> deleteOneEntityByCorrelation(String correlationId) {
        logger.debug("Deleting OneEntity by correlation id: {}", correlationId);

        return msOneWebClient.delete()
                .uri("/entities/correlation/{String correlationId}", correlationId)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Flux<TwoEntity> createRandomNumberOfTwoEntitiesForOneEntityId(String correlationId, String bodyTwo, UUID oneEntityId, boolean tryUnstable) {
        String prefix = tryUnstable ? "Will try to" : "Will";
        int numOfEntitiesToCreate = (new Random()).nextInt(OrchestrationOneConstants.MAX_NUM_OF_CREATED_ENTITIES) + 1;
        logger.debug("{} create {} of TwoEntity instances for oneEntityId: {}", prefix, numOfEntitiesToCreate, oneEntityId);

        return Flux.range(1, numOfEntitiesToCreate)
                .flatMap(i -> {
                    if (tryUnstable) {
                        boolean isUnstable = (new Random()).nextBoolean();
                        if (isUnstable) {
                            logger.debug("Forced unstable creation of TwoEntity for oneEntityId: {}", oneEntityId);
                            return createTwoEntity(new TwoEntity(correlationId, bodyTwo + "x".repeat(20), oneEntityId));
                        }
                        logger.debug("Try unstable caused no damage for oneEntity: {}", oneEntityId);
                    }
                    logger.debug("Forced stable creation of TwoEntity for oneEntityId: {}", oneEntityId);
                    return createTwoEntity(new TwoEntity(correlationId, bodyTwo, oneEntityId));
                });
    }

    public Mono<?> deleteTwoEntitiesByCorrelationId(String correlationId) {
        logger.debug("Deleting TwoEntities by correlationId: {}", correlationId);

        return msTwoWebClient.delete()
                .uri("/entities/correlation/{correlationId}", correlationId)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<TwoEntity> createTwoEntity(TwoEntity entity) {
        return msTwoWebClient.post()
                .uri("/entities")
                .body(Mono.just(entity), TwoEntity.class)
                .retrieve()
                .bodyToMono(TwoEntity.class);
    }
}
