package com.vikas.cowselling.controller;

import com.vikas.cowselling.dto.request.response.CowImageResponse;
import com.vikas.cowselling.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cows")
public class ImageController {

    private final ImageService imageService;

    public ImageController(
            ImageService imageService
    ) {
        this.imageService = imageService;
    }

    @PostMapping(
            value = "/{cowId}/images",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<List<CowImageResponse>>
    uploadImages(
            @PathVariable Long cowId,
            @RequestParam("files")
            List<MultipartFile> files,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                imageService.uploadImages(
                        cowId,
                        files,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long imageId,
            Authentication authentication
    ) {

        imageService.deleteImage(
                imageId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping(
            "/images/{imageId}/primary"
    )
    public ResponseEntity<CowImageResponse>
    setPrimaryImage(
            @PathVariable Long imageId,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                imageService.setPrimaryImage(
                        imageId,
                        authentication.getName()
                )
        );
    }
}

