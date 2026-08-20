package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.CreateCowRequest;
import com.vikas.cowselling.dto.request.UpdateCowRequest;
import com.vikas.cowselling.dto.request.response.CowResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CowService {

    CowResponse createCow(CreateCowRequest request, String sellerEmail);

    PageResponse<CowResponse> getAllAvailableCows(
            int page,
            int size,
            String sortBy,
            String direction,
            String breed,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String city,
            String state,
            String search
    );


    CowResponse getCowById(Long cowId);

    List<CowResponse> getMyCows(String sellerEmail);

    CowResponse updateCow(Long cowId, UpdateCowRequest request, String sellerEmail);

    void deleteCow(Long cowId, String sellerEmail);

    CowResponse markCowAsSold(Long cowId, String sellerEmail);

    CowResponse resubmitCow(Long cowId, String sellerEmail);

}
