package com.boczek.c8demo.orchestrator.two.workers;

import com.boczek.c8demo.orchestrator.two.OrchestratorTwoConstants;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.boczek.c8demo.orchestrator.two.OrchestratorTwoConstants.VARIABLE_NAME_CLMK_AUFGABEN;

@Component
@SuppressWarnings("unused")
public class AuftragsbearbeitungCLMKCheckWorker {

    private static final Logger logger = LoggerFactory.getLogger(AuftragsbearbeitungCLMKCheckWorker.class);

    @JobWorker(type = "auftragsbearbeitungCLMKCheckWorker", autoComplete = false)
    public void auftragsbearbeitungCLMKCheckWorker(final JobClient client, final ActivatedJob job) {

        var clmkAufgaben = List.of(
                "Auftragsbearbeitung im Markt",
                "Auftragsbearbeitung von Abteilung-X",
                "Auftragsbearbeitung von Abteilung-Y"
        );

        client.newCompleteCommand(job.getKey())
                .variable(VARIABLE_NAME_CLMK_AUFGABEN, clmkAufgaben)
                .send()
                .exceptionally(cause -> {
                    throw new RuntimeException(cause);
                });

        logger.debug("Worker auftragsbearbeitungCLMKCheckWorker finished (Process id: {}, Job id: {})", job.getProcessInstanceKey(), job.getKey());
    }
}