package com.vikas.cowselling.dto.request.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String token;

    private String tokenType;

    private UserResponse user;
}
