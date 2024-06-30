package com.boczek.c8demo.scenarios.group1.services;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;

import java.time.Duration;

import static org.assertj.core.api.Fail.fail;

public class ProcessInstanceTestHelper {

    public static ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId, ProcessInstanceVariables variables) {

        var processInstance = client.newCreateInstanceCommand()
                .bpmnProcessId(bpmnProcessId)
                .latestVersion()
                .variables(variables)
                .send().join();

        BpmnAssert.assertThat(processInstance)
                .isStarted()
                .isActive();

        return processInstance;
    }

    public static ProcessInstanceEvent startProcessInstance(ZeebeClient client, String bpmnProcessId, String startingPoint, ProcessInstanceVariables variables) {
        var processInstance = client.newCreateInstanceCommand()
                .bpmnProcessId(bpmnProcessId)
                .latestVersion()
                .variables(variables)
                .startBeforeElement(startingPoint)
                .send().join();

        BpmnAssert.assertThat(processInstance)
                .isStarted()
                .isActive();

        return processInstance;
    }

    public static void completeJob(ZeebeClient client, ZeebeTestEngine engine, String type, int count, JobHandler worker) throws Exception {
        var activateJobsResponse = client.newActivateJobsCommand()
                .jobType(type)
                .maxJobsToActivate(count)
                .send().join();

        var activatedJobs = activateJobsResponse.getJobs();
        if (activatedJobs.size() != count) {
            fail("No jobs activated for type %s".formatted(type));
        }

        for (ActivatedJob job : activatedJobs) {
            worker.handle(client, job);
            client.newCompleteCommand(job)
                    .send().join();
        }

        engine.waitForIdleState(Duration.ofSeconds(5));
    }

    public static void completeJobWithError(ZeebeClient client, ZeebeTestEngine engine, String type, int count, JobHandler worker, String errorCode) throws Exception {
        var activateJobsResponse = client.newActivateJobsCommand()
                .jobType(type)
                .maxJobsToActivate(count)
                .send().join();

        var activatedJobs = activateJobsResponse.getJobs();
        if (activatedJobs.size() != count) {
            fail("No jobs activated for type %s".formatted(type));
        }

        for (ActivatedJob job : activatedJobs) {
            client.newThrowErrorCommand(job)
                    .errorCode(errorCode)
                    .send().join();
        }

        engine.waitForIdleState(Duration.ofSeconds(5));
    }

    public static void completeUserTask(ZeebeClient client, int count, ProcessInstanceVariables variables) {
        var activateJobsResponse = client.newActivateJobsCommand()
                .jobType("io.camunda.zeebe:userTask")
                .maxJobsToActivate(count)
                .send().join();

        var activatedJobs = activateJobsResponse.getJobs();
        if (activatedJobs.size() != count) {
            fail("No user task found");
        }

        for (ActivatedJob job : activatedJobs) {
            client.newCompleteCommand(job)
                    .variables(variables)
                    .send().join();
        }
    }
}
