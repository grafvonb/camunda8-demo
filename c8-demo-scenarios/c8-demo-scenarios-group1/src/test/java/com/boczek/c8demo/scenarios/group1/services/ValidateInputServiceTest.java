package com.boczek.c8demo.scenarios.group1.services;

import com.boczek.c8demo.scenarios.group1.ProcessInstanceVariables;
import com.boczek.c8demo.scenarios.group1.exceptions.ContentTooLongException;
import com.boczek.c8demo.scenarios.group1.exceptions.InvalidContentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ValidateInputServiceTest {

    @Autowired
    private ValidateInputService service;

    @Test
    void validateContentShouldReturnTrueIfContentOk() throws InvalidContentException, ContentTooLongException {
        var variables = new ProcessInstanceVariables();
        variables.setContent("content ok");

        assertTrue(service.validateContent(variables));
    }

    @Test
    void validateContentShouldThrowExceptionIfContentEmptyOrBlank() {
        var variables = new ProcessInstanceVariables();
        variables.setContent("  ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateContent(variables));

        assertEquals("The content must not be null or empty!", exception.getMessage());
    }

    @Test
    void validateContentShouldThrowExceptionIfContentTooLong() {
        var variables = new ProcessInstanceVariables();
        variables.setContent("a".repeat(ValidateInputService.VALIDATE_INPUT_MAX_CHARS + 10));

        ContentTooLongException exception = assertThrows(ContentTooLongException.class, () -> service.validateContent(variables));

        assertEquals("The content must not be longer as 30", exception.getMessage());
    }

    @Test
    void validateContentShouldThrowExceptionIfContentIsInvalid() {
        var variables = new ProcessInstanceVariables();
        variables.setContent("wrong content: gambling");

        InvalidContentException exception = assertThrows(InvalidContentException.class, () -> service.validateContent(variables));

        assertTrue(exception.getMessage().startsWith("The content must not contain one of the following words:"));
    }
}