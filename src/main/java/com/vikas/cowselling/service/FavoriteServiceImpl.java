package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.response.CowImageResponse;
import com.vikas.cowselling.dto.request.response.CowResponse;
import com.vikas.cowselling.dto.request.response.FavoriteResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.Favorite;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.CowStatus;
import com.vikas.cowselling.exception.BadRequestException;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import com.vikas.cowselling.repository.CowRepository;
import com.vikas.cowselling.repository.FavoriteRepository;
import com.vikas.cowselling.repository.UserRepository;
import com.vikas.cowselling.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl
        implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final CowRepository cowRepository;
    private final UserRepository userRepository;

    public FavoriteServiceImpl(
            FavoriteRepository favoriteRepository,
            CowRepository cowRepository,
            UserRepository userRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.cowRepository = cowRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addFavorite(
            Long cowId,
            String userEmail
    ) {

        User user = getUser(userEmail);

        Cow cow = cowRepository.findById(cowId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cow not found"
                        )
                );

        if (cow.getStatus() != CowStatus.AVAILABLE) {
            throw new BadRequestException(
                    "Only available cows can be added to favorites"
            );
        }

        if (favoriteRepository
                .existsByUserIdAndCowId(
                        user.getId(),
                        cowId
                )) {

            throw new BadRequestException(
                    "Cow already exists in favorites"
            );
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .cow(cow)
                .build();

        favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(
            Long cowId,
            String userEmail
    ) {

        User user = getUser(userEmail);

        Favorite favorite =
                favoriteRepository
                        .findByUserIdAndCowId(
                                user.getId(),
                                cowId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Favorite not found"
                                )
                        );

        favoriteRepository.delete(favorite);
    }

    @Override
    public PageResponse<FavoriteResponse> getMyFavorites(
            String userEmail,
            int page,
            int size
    ) {

        User user = getUser(userEmail);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Favorite> favoritePage =
                favoriteRepository.findByUserId(
                        user.getId(),
                        pageable
                );

        List<FavoriteResponse> content =
                favoritePage.getContent()
                        .stream()
                        .map(favorite ->
                                FavoriteResponse.builder()
                                        .id(favorite.getId())
                                        .cow(mapCow(
                                                favorite.getCow()
                                        ))
                                        .createdAt(
                                                favorite.getCreatedAt()
                                        )
                                        .build()
                        )
                        .toList();

        return PageResponse.<FavoriteResponse>builder()
                .content(content)
                .page(favoritePage.getNumber())
                .size(favoritePage.getSize())
                .totalElements(
                        favoritePage.getTotalElements()
                )
                .totalPages(
                        favoritePage.getTotalPages()
                )
                .first(favoritePage.isFirst())
                .last(favoritePage.isLast())
                .hasNext(favoritePage.hasNext())
                .hasPrevious(
                        favoritePage.hasPrevious()
                )
                .build();
    }

    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private CowResponse mapCow(Cow cow) {

        return CowResponse.builder()
                .id(cow.getId())
                .name(cow.getName())
                .breed(cow.getBreed())
                .gender(cow.getGender())
                .age(cow.getAge())
                .price(cow.getPrice())
                .milkProduction(cow.getMilkProduction())
                .weight(cow.getWeight())
                .color(cow.getColor())
                .description(cow.getDescription())
                .city(cow.getCity())
                .state(cow.getState())
                .status(cow.getStatus())
                .sellerId(cow.getSeller().getId())
                .sellerName(cow.getSeller().getName())
                .images(
                        cow.getImages()
                                .stream()
                                .map(image ->
                                        CowImageResponse.builder()
                                                .id(image.getId())
                                                .imageUrl(
                                                        image.getImageUrl()
                                                )
                                                .primaryImage(
                                                        image.getPrimaryImage()
                                                )
                                                .build()
                                )
                                .toList()
                )
                .createdAt(cow.getCreatedAt())
                .updatedAt(cow.getUpdatedAt())
                .build();
    }

}

