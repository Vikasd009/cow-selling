package com.vikas.cowselling.dto.request;

import com.vikas.cowselling.enums.CowGender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCowRequest {

    @NotBlank(message = "Cow name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Breed is required")
    @Size(max = 100, message = "Breed cannot exceed 100 characters")
    private String breed;

    @NotNull(message = "Gender is required")
    private CowGender gender;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 30, message = "Age must be less than or equal to 30 years")
    private Integer age;

    @NotNull(message = "Price is required")
    @DecimalMin( value = "0.0", inclusive = false, message = "Price must be greater than 0" )
    private BigDecimal price;

    @NotNull(message = "Milk production is required")
    @DecimalMin( value = "0.0", inclusive = true, message = "Milk production cannot be negative" )
    private Double milkProduction;

    @DecimalMin( value = "0.0", inclusive = false, message = "Weight must be greater than 0" )
    private Double weight;

    @Size(max = 50, message = "Color cannot exceed 50 characters")
    private String color;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;
}
