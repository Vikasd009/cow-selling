package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.CreateEnquiryRequest;
import com.vikas.cowselling.dto.request.response.EnquiryResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.Enquiry;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.CowStatus;
import com.vikas.cowselling.enums.EnquiryStatus;
import com.vikas.cowselling.enums.NotificationType;
import com.vikas.cowselling.exception.BadRequestException;
import com.vikas.cowselling.exception.ForbiddenException;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import com.vikas.cowselling.repository.CowRepository;
import com.vikas.cowselling.repository.EnquiryRepository;
import com.vikas.cowselling.repository.UserRepository;
import com.vikas.cowselling.service.EnquiryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnquiryServiceImpl implements EnquiryService {

    private final EnquiryRepository enquiryRepository;
    private final CowRepository cowRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public EnquiryServiceImpl(
            EnquiryRepository enquiryRepository,
            CowRepository cowRepository,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.enquiryRepository = enquiryRepository;
        this.cowRepository = cowRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public EnquiryResponse createEnquiry(
            Long cowId,
            CreateEnquiryRequest request,
            String buyerEmail
    ) {

        User buyer = getUserByEmail(buyerEmail);

        Cow cow = cowRepository.findById(cowId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cow not found"
                        )
                );

        if (cow.getStatus() != CowStatus.AVAILABLE) {
            throw new BadRequestException(
                    "Enquiry can only be sent for available cows"
            );
        }

        if (cow.getSeller().getId().equals(buyer.getId())) {
            throw new BadRequestException(
                    "You cannot send an enquiry for your own listing"
            );
        }

        Enquiry enquiry = Enquiry.builder()
                .message(request.getMessage().trim())
                .status(EnquiryStatus.NEW)
                .cow(cow)
                .buyer(buyer)
                .build();

        Enquiry savedEnquiry =
                enquiryRepository.save(enquiry);

        notificationService.createNotification(
                cow.getSeller(),
                "You received a new enquiry for '" +
                        cow.getName() + "'.",
                NotificationType.NEW_ENQUIRY
        );

        return mapToResponse(savedEnquiry);
    }

    @Override
    public PageResponse<EnquiryResponse> getSellerEnquiries(
            String sellerEmail,
            int page,
            int size
    ) {

        User seller = getUserByEmail(sellerEmail);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Enquiry> enquiryPage =
                enquiryRepository.findByCowSeller(
                        seller,
                        pageable
                );

        List<EnquiryResponse> responses =
                enquiryPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return createPageResponse(
                enquiryPage,
                responses
        );
    }

    @Override
    public PageResponse<EnquiryResponse> getMyEnquiries(
            String buyerEmail,
            int page,
            int size
    ) {

        User buyer = getUserByEmail(buyerEmail);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Enquiry> enquiryPage =
                enquiryRepository.findByBuyer(
                        buyer,
                        pageable
                );

        List<EnquiryResponse> responses =
                enquiryPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return createPageResponse(
                enquiryPage,
                responses
        );
    }

    @Override
    public EnquiryResponse updateEnquiryStatus(
            Long enquiryId,
            EnquiryStatus status,
            String sellerEmail
    ) {

        Enquiry enquiry =
                enquiryRepository.findById(enquiryId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Enquiry not found"
                                )
                        );

        if (!enquiry.getCow()
                .getSeller()
                .getEmail()
                .equals(sellerEmail)) {

            throw new ForbiddenException(
                    "You cannot update this enquiry"
            );
        }

        enquiry.setStatus(status);

        Enquiry updatedEnquiry =
                enquiryRepository.save(enquiry);

        notificationService.createNotification(
                updatedEnquiry.getBuyer(),
                "Your enquiry for '" +
                        updatedEnquiry.getCow().getName() +
                        "' has been updated to " +
                        status,
                NotificationType.ENQUIRY_UPDATED
        );

        return mapToResponse(updatedEnquiry);
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private EnquiryResponse mapToResponse(
            Enquiry enquiry
    ) {

        return EnquiryResponse.builder()
                .id(enquiry.getId())
                .message(enquiry.getMessage())
                .status(enquiry.getStatus())

                .cowId(enquiry.getCow().getId())
                .cowName(enquiry.getCow().getName())

                .buyerId(enquiry.getBuyer().getId())
                .buyerName(enquiry.getBuyer().getName())
                .buyerEmail(enquiry.getBuyer().getEmail())

                .createdAt(enquiry.getCreatedAt())
                .updatedAt(enquiry.getUpdatedAt())
                .build();
    }

    private PageResponse<EnquiryResponse> createPageResponse(
            Page<Enquiry> page,
            List<EnquiryResponse> content
    ) {

        return PageResponse.<EnquiryResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}

