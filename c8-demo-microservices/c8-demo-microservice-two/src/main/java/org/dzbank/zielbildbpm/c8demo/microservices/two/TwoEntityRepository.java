package org.dzbank.zielbildbpm.c8demo.microservices.two;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TwoEntityRepository extends JpaRepository<TwoEntity, UUID> {

    List<TwoEntity> findTwoEntitiesByOneEntityId(UUID oneEntityId);

    @Transactional
    void deleteTwoEntitiesByCorrelationId(String correlationId);
}