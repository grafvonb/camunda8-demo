package org.dzbank.zielbildbpm.c8demo.orchestrator.one.services;

import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.OneEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
