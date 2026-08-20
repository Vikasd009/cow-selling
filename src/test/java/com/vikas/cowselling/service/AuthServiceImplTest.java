package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.LoginRequest;
import com.vikas.cowselling.dto.request.RegisterRequest;
import com.vikas.cowselling.dto.request.response.AuthResponse;
import com.vikas.cowselling.dto.request.response.UserResponse;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.UserRole;
import com.vikas.cowselling.exception.BadRequestException;
import com.vikas.cowselling.repository.UserRepository;
import com.vikas.cowselling.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {

        registerRequest =
                new RegisterRequest();

        registerRequest.setName("Vikas");
        registerRequest.setEmail(
                "vikas@example.com"
        );
        registerRequest.setPassword(
                "Password@123"
        );
        registerRequest.setPhoneNumber(
                "9999999999"
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        when(userRepository.existsByEmail(
                registerRequest.getEmail()
        )).thenReturn(false);

        when(passwordEncoder.encode(
                registerRequest.getPassword()
        )).thenReturn(
                "encoded-password"
        );

        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setName(
                registerRequest.getName()
        );
        savedUser.setEmail(
                registerRequest.getEmail()
        );
        savedUser.setPassword(
                "encoded-password"
        );
        savedUser.setRole(UserRole.SELLER);

        when(userRepository.save(
                any(User.class)
        )).thenReturn(savedUser);

        when(jwtService.generateToken(
                any(UserDetails.class)
        )).thenReturn(
                "jwt-token"
        );

        UserResponse response =
                authService.register(
                        registerRequest
                );

        assertNotNull(response);

        assertEquals(
                "jwt-token",
                response.getActive()
        );

        verify(
                userRepository
        ).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail(
                registerRequest.getEmail()
        )).thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> authService.register(
                        registerRequest
                )
        );

        verify(
                userRepository,
                never()
        ).save(any());
    }
}
