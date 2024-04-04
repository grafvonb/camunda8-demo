package org.dzbank.zielbildbpm.c8demo.orchestrator.one;

public interface OrchestrationOneConstants {

    int MAX_NUM_OF_CREATED_ENTITIES = 5;

    String VARIABLE_NAME_BUSINESS_CORRELATION_ID = "businessCorrelationId";

    String MESSAGE_NAME_RUN_TASK_IN_PARALLEL = "runTaskInParallel";
    String MESSAGE_NAME_RUN_TASK_IN_PARALLEL_COMPLETED = "runTaskInParallelCompleted";
}
