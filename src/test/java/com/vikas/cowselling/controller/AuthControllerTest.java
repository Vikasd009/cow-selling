package com.vikas.cowselling.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vikas.cowselling.dto.request.LoginRequest;
import com.vikas.cowselling.dto.request.response.AuthResponse;
import com.vikas.cowselling.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request =
                new LoginRequest();

        request.setEmail(
                "seller@example.com"
        );

        request.setPassword(
                "Password@123"
        );

        AuthResponse response =
                AuthResponse.builder()
                        .token(
                                "test-jwt-token"
                        )
                        .build();

        when(authService.login(
                any(LoginRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.accessToken"
                        ).value(
                                "test-jwt-token"
                        )
                );
    }
}
