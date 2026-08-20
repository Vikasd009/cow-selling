package com.vikas.cowselling.controller;

import com.vikas.cowselling.dto.request.response.FavoriteResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.service.FavoriteService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(
            FavoriteService favoriteService
    ) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{cowId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long cowId,
            Authentication authentication
    ) {

        favoriteService.addFavorite(
                cowId,
                authentication.getName()
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cowId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long cowId,
            Authentication authentication
    ) {

        favoriteService.removeFavorite(
                cowId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<FavoriteResponse>>
    getMyFavorites(
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
                favoriteService.getMyFavorites(
                        authentication.getName(),
                        page,
                        size
                )
        );
    }

}

