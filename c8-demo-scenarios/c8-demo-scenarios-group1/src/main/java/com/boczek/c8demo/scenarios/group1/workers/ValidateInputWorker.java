package com.boczek.c8demo.scenarios.group1.workers;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import com.boczek.c8demo.scenarios.group1.exceptions.ContentTooLongException;
import com.boczek.c8demo.scenarios.group1.exceptions.InvalidContentException;
import com.boczek.c8demo.scenarios.group1.services.ValidateInputService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ValidateInputWorker {

    private static final Logger logger = LoggerFactory.getLogger(ValidateInputWorker.class);

    private static final String CONTENT_TOO_LONG_ERROR_CODE = "contentTooLongError";
    private static final String INVALID_CONTENT_ERROR_CODE = "invalidContentError";

    private ValidateInputService inputService;

    public ValidateInputWorker(ValidateInputService inputService) {
        this.inputService = inputService;
    }

    @JobWorker(type = "validateInputWorker", autoComplete = false)
    @SuppressWarnings("unused")
    public void handle(final JobClient client, final ActivatedJob job) {
        logger.debug("Starting validation...");

        var variables = job.getVariablesAsType(ProcessInstanceVariables.class);

        try {

            var isApproved = inputService.validateContent(variables);

            variables.setInputApproved(isApproved);
            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job " + job, throwable);
                    });
        } catch (ContentTooLongException ex) {
            logger.debug("Validation failed due to: {}", ex.getMessage());
            client.newThrowErrorCommand(job.getKey())
                    .errorCode(CONTENT_TOO_LONG_ERROR_CODE)
                    .errorMessage(ex.getMessage())
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job " + job, throwable);
                    });
        } catch (InvalidContentException ex) {
            logger.debug("Validation failed due to: {}", ex.getMessage());
            client.newThrowErrorCommand(job.getKey())
                    .errorCode(INVALID_CONTENT_ERROR_CODE)
                    .errorMessage(ex.getMessage())
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job " + job, throwable);
                    });
        } catch (Exception ex) {
            var retries = job.getRetries();
            logger.debug("Validation failed due to: {}, retries left: {}", ex.getMessage(), retries - 1);
            client.newFailCommand(job.getKey())
                    .retries(retries - 1)
                    .variables(variables)
                    .errorMessage(ex.getMessage())
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job " + job, throwable);
                    });
        }

        logger.debug("Validation completed. Process instance variables: {}", variables);
    }
}
