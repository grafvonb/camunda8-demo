package org.dzbank.zielbildbpm.c8demo.microservices.two;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TwoEntityService {

    private static final Logger logger = LoggerFactory.getLogger(TwoEntityService.class);

    public static final String BODY_MUST_CONTAIN = "two";

    private final TwoEntityRepository repository;

    public TwoEntityService(TwoEntityRepository repository) {
        this.repository = repository;
    }

    public Flux<TwoEntity> findAllEntities() {
        return Flux.defer(() -> Flux.fromIterable(repository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<TwoEntity> findAllEntitiesByOneEntityId(UUID oneEntityId) {
        return Flux.defer(() -> Flux.fromIterable(repository.findTwoEntitiesByOneEntityId(oneEntityId)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<TwoEntity> findTwoEntity(UUID id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<TwoEntity> saveEntity(TwoEntity entity) {
        return Mono.fromCallable(() -> {
                    if (!entity.getBodyTwo().contains(BODY_MUST_CONTAIN)) {
                        throw new TwoEntityBodyDoesNotContainException(BODY_MUST_CONTAIN);
                    }
                    return repository.save(entity);
                }
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteEntity(TwoEntity entity) {
        return Mono.fromRunnable(() -> repository.delete(entity))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> deleteTwoEntitiesByCorrelationId(String correlationId) {
        return Mono.fromRunnable(() -> repository.deleteTwoEntitiesByCorrelationId(correlationId))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }
}
