package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.CreateCowRequest;
import com.vikas.cowselling.dto.request.UpdateCowRequest;
import com.vikas.cowselling.dto.request.response.CowImageResponse;
import com.vikas.cowselling.dto.request.response.CowResponse;
import com.vikas.cowselling.dto.request.response.PageResponse;
import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.CowStatus;
import com.vikas.cowselling.exception.BadRequestException;
import com.vikas.cowselling.exception.ForbiddenException;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import com.vikas.cowselling.repository.CowRepository;
import com.vikas.cowselling.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.vikas.cowselling.specification.CowSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CowServiceImpl implements CowService{

    private final CowRepository cowRepository;
    private final UserRepository userRepository;

    public CowServiceImpl(CowRepository cowRepository, UserRepository userRepository){
        this.cowRepository = cowRepository;
        this.userRepository = userRepository;
    }


    @Override
    public CowResponse createCow(CreateCowRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        Cow cow = Cow.builder()
                .name(request.getName())
                .breed(request.getBreed())
                .gender(request.getGender())
                .age(request.getAge())
                .price(request.getPrice())
                .milkProduction(request.getMilkProduction())
                .weight(request.getWeight())
                .color(request.getColor())
                .description(request.getDescription())
                .city(request.getCity())
                .state(request.getState())
                .status(CowStatus.PENDING)
                .seller(seller)
                .build();

        Cow savedCow = cowRepository.save(cow);

        return mapToResponse(savedCow);
    }

    @Override
    public PageResponse<CowResponse> getAllAvailableCows(
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
    ) {

        if(!isValidSortField(sortBy)){
            sortBy = "createdAt";
        }

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new BadRequestException(
                    "Minimum price cannot be greater than maximum price"
            );
        }


        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<Cow> specification =
                Specification
                        .where(
                                CowSpecification.hasStatus(
                                        CowStatus.AVAILABLE
                                )
                        )
                        .and(
                                CowSpecification.hasBreed(breed)
                        )
                        .and(
                                CowSpecification.priceGreaterThanOrEqual(
                                        minPrice
                                )
                        )
                        .and(
                                CowSpecification.priceLessThanOrEqual(
                                        maxPrice
                                )
                        )
                        .and(
                                CowSpecification.hasCity(city)
                        )
                        .and(
                                CowSpecification.hasState(state)
                        )
                        .and(
                                CowSpecification.searchByNameOrBreed(
                                        search
                                )
                        );

        Page<Cow> cowPage =
                cowRepository.findAll(
                        specification,
                        pageable
                );

        List<CowResponse> cows =
                cowPage
                        .getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<CowResponse>builder()
                .content(cows)
                .page(cowPage.getNumber())
                .size(cowPage.getSize())
                .totalElements(
                        cowPage.getTotalElements()
                )
                .totalPages(
                        cowPage.getTotalPages()
                )
                .first(cowPage.isFirst())
                .last(cowPage.isLast())
                .hasNext(cowPage.hasNext())
                .hasPrevious(cowPage.hasPrevious())
                .build();
    }


    @Override
    public CowResponse getCowById(Long cowId) {
        Cow cow = cowRepository
                .findById(cowId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cow not found with id: " + cowId
                        )
                );

        if (cow.getStatus() != CowStatus.AVAILABLE) {
            throw new ResourceNotFoundException(
                    "Cow not found"
            );
        }

        return mapToResponse(cow);
    }


    @Override
    public List<CowResponse> getMyCows(String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        return cowRepository.findBySeller(seller)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CowResponse updateCow(Long cowId, UpdateCowRequest request, String sellerEmail) {
        Cow cow = getCowOwnedBySeller( cowId, sellerEmail );

        if (cow.getStatus() == CowStatus.SOLD) {
            throw new BadRequestException( "Sold cow listing cannot be updated" );}

        cow.setName(request.getName());
        cow.setBreed(request.getBreed());
        cow.setGender(request.getGender());
        cow.setAge(request.getAge());
        cow.setPrice(request.getPrice());
        cow.setMilkProduction(request.getMilkProduction());
        cow.setWeight(request.getWeight());
        cow.setColor(request.getColor());
        cow.setDescription(request.getDescription());
        cow.setCity(request.getCity());
        cow.setState(request.getState());

        if (cow.getStatus() == CowStatus.AVAILABLE
                || cow.getStatus() == CowStatus.REJECTED) {

            cow.setStatus(CowStatus.PENDING);

            cow.setRejectionReason(null);
        }

        Cow updatedCow = cowRepository.save(cow);

        return mapToResponse(updatedCow);
    }

    @Override
    public CowResponse resubmitCow(
            Long cowId,
            String sellerEmail
    ) {
        Cow cow = getCowOwnedBySeller(
                cowId,
                sellerEmail
        );

        if (cow.getStatus() != CowStatus.REJECTED) {
            throw new BadRequestException(
                    "Only rejected cow listings can be resubmitted"
            );
        }

        if (cow.getImages() == null
                || cow.getImages().isEmpty()) {

            throw new BadRequestException(
                    "At least one cow image is required before resubmission"
            );
        }

        cow.setStatus(CowStatus.PENDING);

        cow.setRejectionReason(null);

        Cow updatedCow =
                cowRepository.save(cow);

        return mapToResponse(updatedCow);
    }


    @Override
    public void deleteCow(Long cowId, String sellerEmail) {
        Cow cow = getCowOwnedBySeller(cowId, sellerEmail);
        cowRepository.delete(cow);
    }

    @Override
    public CowResponse markCowAsSold(Long cowId, String sellerEmail) {
        Cow cow = getCowOwnedBySeller(cowId, sellerEmail);

        if (cow.getStatus() == CowStatus.SOLD) {
            throw new BadRequestException(
                    "Cow is already marked as sold"
            );
        }

        if (cow.getStatus() != CowStatus.AVAILABLE) {
            throw new BadRequestException(
                    "Only available cows can be marked as sold"
            );
        }

        cow.setStatus(CowStatus.SOLD);

        Cow updatedCow = cowRepository.save(cow);

        return mapToResponse(updatedCow);
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


    private Cow getCowOwnedBySeller(Long cowId, String sellerEmail) {
        Cow cow = cowRepository
                .findById(cowId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cow not found with id: " + cowId
                        )
                );

        User seller = userRepository
                .findByEmail(sellerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seller not found"
                        )
                );

        if (!cow.getSeller().getId().equals(seller.getId())) {
            throw new ForbiddenException(
                    "You are not allowed to modify this cow listing"
            );
        }

        return cow;
    }

    private boolean isValidSortField(String sortBy) {

        return sortBy.equals("price")
                || sortBy.equals("age")
                || sortBy.equals("milkProduction")
                || sortBy.equals("createdAt");
    }

}
