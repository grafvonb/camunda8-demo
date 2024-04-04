package org.dzbank.zielbildbpm.c8demo.orchestrator.one;

import io.camunda.zeebe.client.ZeebeClient;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.services.OrchestratorOneService;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers.reactive.CompensatePersistedDataReactiveWorker;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers.reactive.PersistReactiveTwoEntitiesReactiveWorker;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers.reactive.RunTaskInParallelWorker;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers.reactive.UpdateTwoEntitiesReactiveWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OrchestratorOneStartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorOneStartupRunner.class);

    private final ZeebeClient client;

    private final OrchestratorOneService oOneService;


    public OrchestratorOneStartupRunner(ZeebeClient client, OrchestratorOneService oOneService) {
        this.client = client;
        this.oOneService = oOneService;
    }

    @Override
    public void run(String... args) {
        logger.debug("Configuring and starting workers manually...");

        client.newWorker()
                .jobType("persistReactiveTwoEntitiesWorker")
                .handler(new PersistReactiveTwoEntitiesReactiveWorker(oOneService))
                .name("Persist Reactive TwoEntities Worker")
                .open();

        client.newWorker()
                .jobType("compensatePersistedDataWorker")
                .handler(new CompensatePersistedDataReactiveWorker(oOneService))
                .name("Compensate Persisted Data Worker")
                .open();

        client.newWorker()
                .jobType("updateTwoEntitiesReactiveWorker")
                .handler(new UpdateTwoEntitiesReactiveWorker(oOneService))
                .name("Update TwoEntities Reactive Worker")
                .open();

        client.newWorker()
                .jobType("runTaskInParallelWorker")
                .handler(new RunTaskInParallelWorker(client))
                .name("Run Task In Parallel Worker")
                .fetchVariables("businessCorrelationId")
                .open();
    }
}
