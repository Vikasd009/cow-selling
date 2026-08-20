package com.vikas.cowselling.dto.request.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FavoriteResponse {

    private Long id;

    private CowResponse cow;

    private LocalDateTime createdAt;
}

