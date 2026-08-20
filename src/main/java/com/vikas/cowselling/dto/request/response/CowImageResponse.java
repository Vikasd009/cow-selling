package com.vikas.cowselling.dto.request.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CowImageResponse {

    private Long id;
    private String imageUrl;
    private Boolean primaryImage;
}
