package com.vikas.cowselling.dto.request.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private T data;

}

