package org.dzbank.zielbildbpm.c8demo.microservices.two;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/entities")
public class TwoEntityController {

    private static final Logger logger = LoggerFactory.getLogger(TwoEntityController.class);

    private final TwoEntityService service;

    public TwoEntityController(TwoEntityService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<TwoEntity> retrieveAll() {
        return service.findAllEntities();
    }

    @GetMapping("/query")
    public Flux<TwoEntity> retrieveAllByOneEntityId(@RequestParam UUID oneEntityId) {
        logger.debug("Querying for TwoEntities by OneEntityId: {}", oneEntityId);

        return service.findAllEntitiesByOneEntityId(oneEntityId);
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<TwoEntity>> retrieveSingle(@PathVariable UUID id) {
        return service.findTwoEntity(id)
                .map(e -> ResponseEntity.ok(e))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<TwoEntity>> create(@RequestBody TwoEntity entity) {
        return service.saveEntity(entity)
                .map(ResponseEntity::ok);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<TwoEntity>> update(@PathVariable UUID id, @RequestBody TwoEntity entity) {
        return service.findTwoEntity(id)
                .flatMap(e -> {
                    e.setBodyTwo(entity.getBodyTwo());
                    e.setCorrelationId(entity.getCorrelationId());
                    return service.saveEntity(e);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable UUID id) {
        logger.debug("Deleting entity with the id: {}", id);

        return service.findTwoEntity(id)
                .flatMap(service::deleteEntity)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/correlation/{correlationId}")
    public Mono<ResponseEntity<Void>> deleteByCorrelationId(@PathVariable String correlationId) {
        logger.debug("Deleting entity by the correlation id: {}", correlationId);

        return service.deleteTwoEntitiesByCorrelationId(correlationId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
