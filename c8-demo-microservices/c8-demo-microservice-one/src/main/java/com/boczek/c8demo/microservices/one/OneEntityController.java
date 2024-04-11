package com.boczek.c8demo.microservices.one;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/entities")
public class OneEntityController {

    private static final Logger logger = LoggerFactory.getLogger(OneEntityController.class);

    private final OneEntityService service;

    public OneEntityController(OneEntityService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<OneEntity> retrieveAll() {
        return service.findAllEntities();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<OneEntity>> retrieveSingle(@PathVariable UUID id) {
        return service.findOneEntity(id)
                .map(e -> ResponseEntity.ok(e))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<OneEntity>> create(@RequestBody OneEntity entity) {
        return service.saveEntity(entity)
                .map(ResponseEntity::ok);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<OneEntity>> update(@PathVariable UUID id, @RequestBody OneEntity entity) {
        return service.findOneEntity(id)
                .flatMap(e -> {
                    e.setBodyOne(entity.getBodyOne());
                    e.setCorrelationId(entity.getCorrelationId());
                    return service.saveEntity(e);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable UUID id) {
        logger.debug("Deleting entity with the id: {}", id);

        return service.findOneEntity(id)
                .flatMap(service::deleteEntity)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/correlation/{correlationId}")
    public Mono<ResponseEntity<Void>> deleteByCorrelationId(@PathVariable String correlationId) {
        logger.debug("Deleting entity by the correlation id: {}", correlationId);

        return service.deleteEntityByCorrelationId(correlationId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
