package com.boczek.c8demo.scenarios.group1.services;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import com.boczek.c8demo.scenarios.group1.ScenariosGroup1Consts;
import com.boczek.c8demo.scenarios.group1.workers.ShortenContentWorker;
import com.boczek.c8demo.scenarios.group1.workers.ValidateInputWorker;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.boczek.c8demo.scenarios.group1.services.ProcessInstanceTestHelper.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ZeebeProcessTest
public class ErrorHandlingProcessTest {

    private ZeebeClient client;

    private ZeebeTestEngine engine;

    @Autowired
    private ValidateInputService validateInputService;

    @BeforeEach
    public void setup() {
        var deploymentEvent = client.newDeployResourceCommand()
                .addResourceFromClasspath("bpmn/C8DemoScenariosErrorHandling.bpmn")
                .send()
                .join();

        BpmnAssert.assertThat(deploymentEvent)
                .containsProcessesByBpmnProcessId(ScenariosGroup1Consts.ERROR_HANDLING_BPMNPROCESSID);
    }

    @Test
    public void deploymentShouldWork() {}

    @Test
    public void processInstanceShouldCompleteIfContentIsValid() throws Exception {
        var variables = new ProcessInstanceVariables();
        variables.setContent("Valid content");

        var processInstance = startProcessInstance(client, ScenariosGroup1Consts.ERROR_HANDLING_BPMNPROCESSID, variables);

        BpmnAssert.assertThat(processInstance)
                .isWaitingAtElements("validateInputTask");

        var validateInputWorker = new ValidateInputWorker(validateInputService);
        completeJob(client, engine,"validateInputWorker", 1, validateInputWorker);

        BpmnAssert.assertThat(processInstance)
                .hasPassedElement("inputAcceptedEndEvent")
                .isCompleted();
    }

    @Test
    public void processInstanceShouldShortenContentIfTooLong() throws Exception {
        var variables = new ProcessInstanceVariables();
        variables.setContent("a".repeat(ValidateInputService.VALIDATE_INPUT_MAX_CHARS + 10));

        var processInstance = startProcessInstance(client, ScenariosGroup1Consts.ERROR_HANDLING_BPMNPROCESSID, variables);

        var validateInputWorker = new ValidateInputWorker(validateInputService);
        completeJob(client, engine, "validateInputWorker", 1, validateInputWorker);

        BpmnAssert.assertThat(processInstance)
                .isWaitingAtElements("shortenContentTask");

        var shortenContentService = new ShortenContentService();
        var shortenContentWorker = new ShortenContentWorker(shortenContentService);
        completeJob(client, engine,"shortenContentWorker", 1, shortenContentWorker);

        BpmnAssert.assertThat(processInstance)
                .hasVariableWithValue("content", "a".repeat(ValidateInputService.VALIDATE_INPUT_MAX_CHARS))
                .isWaitingAtElements("validateInputTask");

        completeJob(client, engine, "validateInputWorker", 1, validateInputWorker);

        BpmnAssert.assertThat(processInstance)
                .hasPassedElement("inputAcceptedEndEvent")
                .isCompleted();
    }

    @Test
    public void processInstanceShouldPassManualReviewIfContentNotValid() throws Exception {
        var variables = new ProcessInstanceVariables();
        variables.setContent("not valid content gambling");

        var processInstance = startProcessInstance(client, ScenariosGroup1Consts.ERROR_HANDLING_BPMNPROCESSID, variables);

        var validateInputWorker = new ValidateInputWorker(validateInputService);
        completeJobWithError(client, engine, "validateInputWorker", 1, validateInputWorker, ValidateInputWorker.INVALID_CONTENT_ERROR_CODE);

        BpmnAssert.assertThat(processInstance)
                .isWaitingAtElements("reviewInputContentTask");

        BpmnAssert.assertThat(processInstance)
                .hasPassedElement("inputAcceptedEndEvent")
                .isCompleted();
    }
}
