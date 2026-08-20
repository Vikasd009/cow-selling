package com.vikas.cowselling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEnquiryRequest {

    @NotBlank(message = "Message is required")
    @Size(
            min = 10,
            max = 1000,
            message = "Message must be between 10 and 1000 characters"
    )
    private String message;
}
