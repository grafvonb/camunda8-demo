package com.boczek.c8demo.scenarios.group1.services;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import com.boczek.c8demo.scenarios.group1.ScenariosGroup1Consts;
import com.boczek.c8demo.scenarios.group1.workers.LogMessageWorker;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static com.boczek.c8demo.scenarios.group1.services.ProcessInstanceTestHelper.*;

@ZeebeProcessTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SimpleUserTaskProcessTest {

    private ZeebeClient client;

    private ZeebeTestEngine engine;

    @BeforeEach
    public void setup() {
        var deploymentEvent = client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/C8DemoScenariosSimpleUserTask.bpmn")
                .send()
                .join();

        BpmnAssert.assertThat(deploymentEvent)
                .containsProcessesByBpmnProcessId(ScenariosGroup1Consts.SIMPLE_USER_TASK_BPMNPROCESSID);
    }

    @Test
    public void deploymentShouldWork() {}

    @Test
    public void processInstanceShouldCompleteIfContentIsValid() throws Exception {
        var variables = new ProcessInstanceVariables();
        variables.setContent("Valid content");

        var processInstance = startProcessInstance(client, ScenariosGroup1Consts.SIMPLE_USER_TASK_BPMNPROCESSID, variables);

        BpmnAssert.assertThat(processInstance)
                .isWaitingAtElements("logMessageTask");
        completeJob(client, engine, "logMessageWorker", 1, new LogMessageWorker());

        engine.waitForIdleState(Duration.ofMinutes(5));

        BpmnAssert.assertThat(processInstance)
                .isWaitingAtElements("validateContentTask");
        completeUserTask(client, 1, variables);

        BpmnAssert.assertThat(processInstance)
                .isCompleted();
    }
}
