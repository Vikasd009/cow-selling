package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.LoginRequest;
import com.vikas.cowselling.dto.request.RegisterRequest;
import com.vikas.cowselling.dto.request.response.AuthResponse;
import com.vikas.cowselling.dto.request.response.UserResponse;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.UserRole;
import com.vikas.cowselling.repository.UserRepository;
import com.vikas.cowselling.security.CustomUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.vikas.cowselling.exception.BadRequestException;
import com.vikas.cowselling.exception.DuplicateResourceException;
import com.vikas.cowselling.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           CustomUserDetailsService customUserDetailsService,
                           JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }


    @Override
    public UserResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email already exists");
        }

        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new DuplicateResourceException("Phone number already exists");
        }

        if(request.getRole() == UserRole.ADMIN){
            throw new BadRequestException("Admin registration is not allowed");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .city(request.getCity())
                .state(request.getState())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .role(savedUser.getRole())
                .city(savedUser.getCity())
                .state(savedUser.getState())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ) );
        UserDetails userDetails = customUserDetailsService .loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        User user = userRepository .findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found") );
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .city(user.getCity())
                .state(user.getState())
                .build();

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }
}
