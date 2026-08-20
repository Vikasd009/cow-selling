package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.response.CowImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    List<CowImageResponse> uploadImages(
            Long cowId,
            List<MultipartFile> files,
            String sellerEmail
    );

    void deleteImage(
            Long imageId,
            String sellerEmail
    );

    CowImageResponse setPrimaryImage(
            Long imageId,
            String sellerEmail
    );

}

