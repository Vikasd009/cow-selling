package com.vikas.cowselling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RejectCowRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 1000, message = "Rejcction reason cannot exceed 1000 characters")
    private String reason;
}
