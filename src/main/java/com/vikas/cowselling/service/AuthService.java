package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.LoginRequest;
import com.vikas.cowselling.dto.request.RegisterRequest;
import com.vikas.cowselling.dto.request.response.AuthResponse;
import com.vikas.cowselling.dto.request.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
