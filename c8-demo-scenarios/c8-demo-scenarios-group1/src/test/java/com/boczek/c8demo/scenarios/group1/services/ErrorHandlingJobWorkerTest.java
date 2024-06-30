package com.boczek.c8demo.scenarios.group1.services;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import com.boczek.c8demo.scenarios.group1.workers.ValidateInputWorker;
import io.camunda.zeebe.client.api.ZeebeFuture;
import io.camunda.zeebe.client.api.command.FinalCommandStep;
import io.camunda.zeebe.client.api.command.ThrowErrorCommandStep1;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ErrorHandlingJobWorkerTest {

    @Autowired
    private ValidateInputService validateInputService;

    @Test
    void testErrorHandling() {
        // Mocks
        JobClient jobClient = mock(JobClient.class);
        ActivatedJob job = mock(ActivatedJob.class);

        ProcessInstanceVariables processVariables = mock(ProcessInstanceVariables.class);
        when(processVariables.getContent()).thenReturn("Invalid content: gambling");
        when(job.getVariablesAsType(ProcessInstanceVariables.class)).thenReturn(processVariables);

        ThrowErrorCommandStep1 commandStep1 = mock(ThrowErrorCommandStep1.class);
        ThrowErrorCommandStep1.ThrowErrorCommandStep2 commandStep2 = mock(ThrowErrorCommandStep1.ThrowErrorCommandStep2.class, Answers.RETURNS_DEEP_STUBS);
        ZeebeFuture<Void> future = mock(ZeebeFuture.class);

        when(jobClient.newThrowErrorCommand(anyLong()))
                .thenReturn(commandStep1);
        when(commandStep1.errorCode(anyString())).thenReturn(commandStep2);
        when(commandStep2.errorMessage(anyString())).thenReturn(commandStep2);
        when(commandStep2.variables(Object.class)).thenReturn(commandStep2);
        when(commandStep2.send()).thenReturn(future);
        when(future.join()).thenReturn(null);

        ValidateInputWorker worker = new ValidateInputWorker(validateInputService);
        worker.handle(jobClient, job);

        // Assertions
        verify(jobClient).newThrowErrorCommand(job.getKey());
        verify(commandStep1).errorCode(ValidateInputWorker.INVALID_CONTENT_ERROR_CODE);
        verify(commandStep2).errorMessage("The content must not contain one of the following words: [violence, drugs, alcohol, gambling, abuse, profanity, hate, terror, nudity, weapons]");
        // verify(commandStep2).send();
    }
}
