package com.vikas.cowselling.controller;

import com.vikas.cowselling.exception.GlobalExceptionHandler;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldReturnNotFoundResponse() {

        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "Cow not found"
                );

        assertNotNull(exception.getMessage());
    }
}
