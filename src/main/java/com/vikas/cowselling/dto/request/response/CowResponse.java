package com.vikas.cowselling.dto.request.response;

import com.vikas.cowselling.enums.CowGender;
import com.vikas.cowselling.enums.CowStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CowResponse {

    private Long id;
    private String name;
    private String breed;
    private CowGender gender;
    private Integer age;
    private BigDecimal price;
    private Double milkProduction;
    private Double weight;
    private String color;
    private String description;
    private String city;
    private String state;
    private CowStatus status;
    private String rejectionReason;

    private Long sellerId;
    private String sellerName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CowImageResponse> images;
}
