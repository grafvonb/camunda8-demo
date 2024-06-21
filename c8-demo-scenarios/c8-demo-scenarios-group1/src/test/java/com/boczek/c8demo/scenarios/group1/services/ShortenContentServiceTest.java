package com.boczek.c8demo.scenarios.group1.services;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ShortenContentServiceTest {

    @Autowired
    private ShortenContentService service;

    @Test
    void shortenContentShouldThrowExceptionIfContentIsNull() {
        var variables = new ProcessInstanceVariables();
        variables.setContent(null);

        var exception = assertThrows(IllegalArgumentException.class, () -> service.shortenContent(variables));
        assertEquals("The content must not be null or empty!", exception.getMessage());
    }

    @Test
    void shortenContentShouldThrowExceptionIfContentIsBlank() {
        var variables = new ProcessInstanceVariables();
        variables.setContent("   ");

        var exception = assertThrows(IllegalArgumentException.class, () -> service.shortenContent(variables));
        assertEquals("The content must not be null or empty!", exception.getMessage());
    }

    @Test
    void shortenContentShouldNotShortenContentIfLenghtOk() {
        var variables = new ProcessInstanceVariables();
        var validContent = "Valid content";
        variables.setContent(validContent);

        var result = service.shortenContent(variables);
        assertEquals(validContent, result);
    }

    @Test
    void shortenContentShouldShortenContentIfTooLong() {
        var variables = new ProcessInstanceVariables();
        variables.setContent("a".repeat(ValidateInputService.VALIDATE_INPUT_MAX_CHARS + 10));

        var result = service.shortenContent(variables);
        assertEquals(ValidateInputService.VALIDATE_INPUT_MAX_CHARS, result.length());
    }
}