package com.boczek.c8demo.scenarios.group1.services;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import com.boczek.c8demo.scenarios.group1.exceptions.ContentTooLongException;
import com.boczek.c8demo.scenarios.group1.exceptions.InvalidContentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidateInputService {

    private static final Logger logger = LoggerFactory.getLogger(ValidateInputService.class);

    public static final int VALIDATE_INPUT_MAX_CHARS = 30;

    public boolean validateContent(ProcessInstanceVariables variables, List<String> inappropriateWords) throws ContentTooLongException, InvalidContentException {


        if (variables.getContent() == null || variables.getContent().isBlank()) {
            throw new IllegalArgumentException("The content must not be null or empty!");
        }

        if (variables.getContent().length() > VALIDATE_INPUT_MAX_CHARS) {
            throw new ContentTooLongException("The content must not be longer as %s".formatted(VALIDATE_INPUT_MAX_CHARS));
        }

        var isGoodForTheWorld = inappropriateWords.stream().noneMatch(variables.getContent().toLowerCase()::contains);
        if (!isGoodForTheWorld) {
            throw new InvalidContentException("The content must not contain one of the following words: %s".formatted(inappropriateWords));
        }

        return true;
    }

}
