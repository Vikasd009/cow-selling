package com.vikas.cowselling.dto.request.response;

import com.vikas.cowselling.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdAt;
    private String city;
    private String state;
}
