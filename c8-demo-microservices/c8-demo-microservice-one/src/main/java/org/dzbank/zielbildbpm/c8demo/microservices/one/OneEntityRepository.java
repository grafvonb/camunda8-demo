package org.dzbank.zielbildbpm.c8demo.microservices.one;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface OneEntityRepository extends JpaRepository<OneEntity, UUID> {

    @Transactional
    void deleteOneEntityByCorrelationId(String correlationId);
}