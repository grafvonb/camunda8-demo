package com.boczek.c8demo.scenarios.group1.workers;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ValidateInputWorker {

    private static final Logger logger = LoggerFactory.getLogger(ValidateInputWorker.class);

    public static final int VALIDATE_INPUT_MAX_CHARS = 30;

    private static final String CONTENT_TOO_LONG_ERROR_CODE = "contentTooLongError";
    private static final String INVALID_CONTENT_ERROR_CODE = "invalidContentError";

    private static final List<String> inappropriateWords = Arrays.asList(
            "violence", "drugs", "alcohol", "gambling", "abuse",
            "profanity", "hate", "terror", "nudity", "weapons"
    );

    @JobWorker(type = "validateInputWorker", autoComplete = false)
    public void handle(final JobClient client, final ActivatedJob job) {
        logger.debug("Starting validation...");

        var variables = job.getVariablesAsType(ProcessInstanceVariables.class);

        if (variables.getContent() == null || variables.getContent().isBlank()) {
            throw new IllegalArgumentException("The content must not be null or empty!");
        }

        if (variables.getContent().length() > VALIDATE_INPUT_MAX_CHARS) {
            client.newThrowErrorCommand(job.getKey())
                    .errorCode(CONTENT_TOO_LONG_ERROR_CODE)
                    .errorMessage("The content must not be longer as %n".formatted(VALIDATE_INPUT_MAX_CHARS))
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job " + job, throwable);
                    });
            return;
        }

        var isGoodForTheWorld = inappropriateWords.stream().noneMatch(variables.getContent().toLowerCase()::contains);
        if (!isGoodForTheWorld) {
            client.newThrowErrorCommand(job.getKey())
                    .errorCode(INVALID_CONTENT_ERROR_CODE)
                    .errorMessage("The content must not contain one of the following words: %s".formatted(inappropriateWords))
                    .send()
                    .exceptionally(throwable -> {
                        throw new RuntimeException("Could not complete job " + job, throwable);
                    });
            return;
        }

        variables.setInputApproved(true);
        client.newCompleteCommand(job.getKey())
                .variables(variables)
                .send()
                .exceptionally(throwable -> {
                    throw new RuntimeException("Could not complete job " + job, throwable);
                });

        logger.debug("Validation completed.");
    }
}
