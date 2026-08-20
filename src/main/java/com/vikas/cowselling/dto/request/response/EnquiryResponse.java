package com.vikas.cowselling.dto.request.response;

import com.vikas.cowselling.enums.EnquiryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EnquiryResponse {

    private Long id;

    private String message;

    private EnquiryStatus status;

    private Long cowId;
    private String cowName;

    private Long buyerId;
    private String buyerName;
    private String buyerEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

