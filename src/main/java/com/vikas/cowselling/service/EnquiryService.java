package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.CreateEnquiryRequest;
import com.vikas.cowselling.dto.request.response.EnquiryResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.enums.EnquiryStatus;

public interface EnquiryService {

    EnquiryResponse createEnquiry(
            Long cowId,
            CreateEnquiryRequest request,
            String buyerEmail
    );

    PageResponse<EnquiryResponse> getSellerEnquiries(
            String sellerEmail,
            int page,
            int size
    );

    PageResponse<EnquiryResponse> getMyEnquiries(
            String buyerEmail,
            int page,
            int size
    );

    EnquiryResponse updateEnquiryStatus(
            Long enquiryId,
            EnquiryStatus status,
            String sellerEmail
    );

}

