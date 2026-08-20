package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.response.FavoriteResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;

public interface FavoriteService {

    void addFavorite(
            Long cowId,
            String userEmail
    );

    void removeFavorite(
            Long cowId,
            String userEmail
    );

    PageResponse<FavoriteResponse> getMyFavorites(
            String userEmail,
            int page,
            int size
    );

}

