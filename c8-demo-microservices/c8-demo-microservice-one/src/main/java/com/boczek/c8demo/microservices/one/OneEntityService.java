package com.boczek.c8demo.microservices.one;

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
public class OneEntityService {

    private static final Logger logger = LoggerFactory.getLogger(OneEntityService.class);

    public static final String BODY_MUST_CONTAIN = "one";

    private final OneEntityRepository repository;

    public OneEntityService(OneEntityRepository repository) {
        this.repository = repository;
    }

    public void initializeWithSomeEntities() {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                    new OneEntity("no-correlation", "init 1 one"),
                    new OneEntity("no-correlation", "init 2 one"),
                    new OneEntity("no-correlation", "init 3 one")
            ));
        }
    }

    public Flux<OneEntity> findAllEntities() {
        return Flux.defer(() -> Flux.fromIterable(repository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<OneEntity> findOneEntity(UUID id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<OneEntity> saveEntity(OneEntity entity) {
        return Mono.fromCallable(() -> {
                    if (!entity.getBodyOne().contains(BODY_MUST_CONTAIN)) {
                        throw new OneEntityBodyDoesNotContainException(BODY_MUST_CONTAIN);
                    }
                    return repository.save(entity);
                }
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteEntity(OneEntity entity) {
        return Mono.fromRunnable(() -> repository.delete(entity))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<Void> deleteEntityByCorrelationId(String correlationId) {
        return Mono.fromRunnable(() -> repository.deleteOneEntityByCorrelationId(correlationId))
                .subscribeOn(Schedulers.boundedElastic()).then();
    }
}
