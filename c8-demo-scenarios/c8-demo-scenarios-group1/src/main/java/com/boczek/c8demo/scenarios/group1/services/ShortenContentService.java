package com.boczek.c8demo.scenarios.group1.services;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShortenContentService {

    private static final Logger logger = LoggerFactory.getLogger(ShortenContentService.class);

    public String shortenContent(ProcessInstanceVariables variables) {

        if (variables.getContent() == null || variables.getContent().isBlank()) {
            throw new IllegalArgumentException("The content must not be null or empty!");
        }

        if (variables.getContent().length() > ValidateInputService.VALIDATE_INPUT_MAX_CHARS) {
            return variables.getContent().substring(0, ValidateInputService.VALIDATE_INPUT_MAX_CHARS);
        }

        return variables.getContent();
    }

}
