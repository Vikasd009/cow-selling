package com.vikas.cowselling.controller;

import com.vikas.cowselling.dto.request.CreateEnquiryRequest;
import com.vikas.cowselling.dto.request.response.EnquiryResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.enums.EnquiryStatus;
import com.vikas.cowselling.service.EnquiryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(
            EnquiryService enquiryService
    ) {
        this.enquiryService = enquiryService;
    }

    @PostMapping("/cows/{cowId}/enquiries")
    public ResponseEntity<EnquiryResponse> createEnquiry(
            @PathVariable Long cowId,
            @Valid @RequestBody CreateEnquiryRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                enquiryService.createEnquiry(
                        cowId,
                        request,
                        authentication.getName()
                )
        );
    }

    @GetMapping("/seller/enquiries")
    public ResponseEntity<PageResponse<EnquiryResponse>>
    getSellerEnquiries(
            Authentication authentication,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(50)
            int size
    ) {

        return ResponseEntity.ok(
                enquiryService.getSellerEnquiries(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @GetMapping("/my-enquiries")
    public ResponseEntity<PageResponse<EnquiryResponse>>
    getMyEnquiries(
            Authentication authentication,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(50)
            int size
    ) {

        return ResponseEntity.ok(
                enquiryService.getMyEnquiries(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

    @PatchMapping("/enquiries/{enquiryId}/status")
    public ResponseEntity<EnquiryResponse>
    updateStatus(
            @PathVariable Long enquiryId,
            @RequestParam EnquiryStatus status,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                enquiryService.updateEnquiryStatus(
                        enquiryId,
                        status,
                        authentication.getName()
                )
        );
    }
}

