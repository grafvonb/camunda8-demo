package com.boczek.c8demo.scenarios.group1.workers;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.random.RandomGenerator;

@Component
@SuppressWarnings("unused")
public class SimulateCrashWorker implements JobHandler {

    private static final Logger logger = LoggerFactory.getLogger(SimulateCrashWorker.class);

    public static final String HEADER_KEY_CRASH_PROBABILITY_IN_PERCENT = "crashProbabilityInPercent";
    public static final String DEFAULT_CRASH_PROBABILITY_IN_PERCENT = "50";

    @JobWorker(type = "simulateWorkerCrash")
    public void handle(final JobClient client, final ActivatedJob job) {

        int crashProbability = Integer.parseInt(job.getCustomHeaders()
                .getOrDefault(HEADER_KEY_CRASH_PROBABILITY_IN_PERCENT, DEFAULT_CRASH_PROBABILITY_IN_PERCENT));

        var nextInt = RandomGenerator.getDefault().nextInt(100);
        if (nextInt <= crashProbability) {
            throw new RuntimeException("Worker encountered an unexpected error (due to generated %s value which was below %s%% crash probability) and has crashed.".formatted(nextInt, crashProbability));
        }

        logger.debug("Worker done (Process id: {}, Job id: {})", job.getProcessInstanceKey(), job.getKey());
    }
}