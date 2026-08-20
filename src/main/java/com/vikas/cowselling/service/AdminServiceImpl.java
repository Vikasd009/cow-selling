package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.response.*;
import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.CowStatus;
import com.vikas.cowselling.enums.NotificationType;
import com.vikas.cowselling.enums.UserRole;
import com.vikas.cowselling.exception.BadRequestException;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import com.vikas.cowselling.repository.CowRepository;
import com.vikas.cowselling.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final CowRepository cowRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AdminServiceImpl(
            CowRepository cowRepository,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.cowRepository = cowRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public PageResponse<CowResponse> getPendingCows(
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").ascending()
        );

        Page<Cow> cowPage =
                cowRepository.findByStatus(
                        CowStatus.PENDING,
                        pageable
                );

        List<CowResponse> responses =
                cowPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<CowResponse>builder()
                .content(responses)
                .page(cowPage.getNumber())
                .size(cowPage.getSize())
                .totalElements(cowPage.getTotalElements())
                .totalPages(cowPage.getTotalPages())
                .first(cowPage.isFirst())
                .last(cowPage.isLast())
                .hasNext(cowPage.hasNext())
                .hasPrevious(cowPage.hasPrevious())
                .build();
    }

    @Override
    public CowResponse approveCow(Long cowId) {

        Cow cow = getCow(cowId);

        if (cow.getStatus() != CowStatus.PENDING) {
            throw new BadRequestException(
                    "Only pending cow listings can be approved"
            );
        }

        cow.setStatus(CowStatus.AVAILABLE);

        Cow updatedCow = cowRepository.save(cow);

        notificationService.createNotification(
                updatedCow.getSeller(),
                "Your cow listing '" +
                        updatedCow.getName() +
                        "' has been approved.",
                NotificationType.COW_APPROVED
        );

        return mapToResponse(updatedCow);
    }

    @Override
    public CowResponse rejectCow(
            Long cowId,
            String reason
    ) {
        Cow cow = getCow(cowId);

        if (cow.getStatus() != CowStatus.PENDING) {
            throw new BadRequestException(
                    "Only pending cow listings can be rejected"
            );
        }

        cow.setStatus(CowStatus.REJECTED);

        cow.setRejectionReason(
                reason.trim()
        );

        Cow updatedCow =
                cowRepository.save(cow);

        notificationService.createNotification(
                updatedCow.getSeller(),
                "Your cow listing '" +
                        updatedCow.getName() +
                        "' was rejected. Reason: " +
                        reason,
                NotificationType.COW_REJECTED
        );

        return mapToResponse(updatedCow);
    }


    @Override
    public PageResponse<UserResponse> getAllUsers(
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<User> userPage =
                userRepository.findAll(pageable);

        List<UserResponse> responses =
                userPage.getContent()
                        .stream()
                        .map(this::mapUserToResponse)
                        .toList();

        return PageResponse.<UserResponse>builder()
                .content(responses)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(
                        userPage.getTotalElements()
                )
                .totalPages(
                        userPage.getTotalPages()
                )
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    @Override
    public UserResponse blockUser(Long userId) {

        User user = getUser(userId);

        if (user.getRole() == UserRole.ADMIN) {
            throw new BadRequestException(
                    "Admin user cannot be blocked"
            );
        }

        if (!user.getActive()) {
            throw new BadRequestException(
                    "User is already blocked"
            );
        }

        user.setActive(false);

        User updatedUser =
                userRepository.save(user);

        return mapUserToResponse(updatedUser);
    }

    @Override
    public UserResponse unblockUser(Long userId) {

        User user = getUser(userId);

        if (user.getActive()) {
            throw new BadRequestException(
                    "User is already active"
            );
        }

        user.setActive(true);

        User updatedUser =
                userRepository.save(user);

        return mapUserToResponse(updatedUser);
    }

    @Override
    public AdminDashboardResponse getDashboardStatistics() {

        long totalUsers =
                userRepository.count();

        long totalSellers =
                userRepository.countByRole(
                        UserRole.SELLER
                );

        long totalCows =
                cowRepository.count();

        long pendingCows =
                cowRepository.countByStatus(
                        CowStatus.PENDING
                );

        long availableCows =
                cowRepository.countByStatus(
                        CowStatus.AVAILABLE
                );

        long soldCows =
                cowRepository.countByStatus(
                        CowStatus.SOLD
                );

        long rejectedCows =
                cowRepository.countByStatus(
                        CowStatus.REJECTED
                );

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalSellers(totalSellers)
                .totalCows(totalCows)
                .pendingCows(pendingCows)
                .availableCows(availableCows)
                .soldCows(soldCows)
                .rejectedCows(rejectedCows)
                .build();
    }



    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );
    }

    private UserResponse mapUserToResponse(
            User user
    ) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }


    private Cow getCow(Long cowId) {

        return cowRepository.findById(cowId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cow not found with id: " + cowId
                        )
                );
    }

    private CowResponse mapToResponse(Cow cow) {

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
                .rejectionReason(cow.getRejectionReason())

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

