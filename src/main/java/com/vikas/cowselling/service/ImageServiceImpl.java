package com.vikas.cowselling.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vikas.cowselling.dto.request.response.CowImageResponse;
import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.CowImage;
import com.vikas.cowselling.exception.BadRequestException;
import com.vikas.cowselling.exception.ForbiddenException;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import com.vikas.cowselling.repository.CowImageRepository;
import com.vikas.cowselling.repository.CowRepository;
import com.vikas.cowselling.repository.UserRepository;
import com.vikas.cowselling.service.ImageService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    private static final int MAX_IMAGES_PER_COW = 10;

    private final Cloudinary cloudinary;
    private final CowRepository cowRepository;
    private final CowImageRepository cowImageRepository;
    private final UserRepository userRepository;

    public ImageServiceImpl(
            Cloudinary cloudinary,
            CowRepository cowRepository,
            CowImageRepository cowImageRepository,
            UserRepository userRepository
    ) {
        this.cloudinary = cloudinary;
        this.cowRepository = cowRepository;
        this.cowImageRepository = cowImageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CowImageResponse> uploadImages(
            Long cowId,
            List<MultipartFile> files,
            String sellerEmail
    ) {

        Cow cow = getCowOwnedBySeller(
                cowId,
                sellerEmail
        );

        if (files == null || files.isEmpty()) {
            throw new BadRequestException(
                    "At least one image is required"
            );
        }

        long existingImages =
                cowImageRepository.countByCowId(cowId);

        if (existingImages + files.size()
                > MAX_IMAGES_PER_COW) {

            throw new BadRequestException(
                    "Maximum " + MAX_IMAGES_PER_COW
                            + " images allowed per cow"
            );
        }

        List<CowImageResponse> responses =
                new ArrayList<>();

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                throw new BadRequestException(
                        "Empty image file is not allowed"
                );
            }

            validateImage(file);

            try {

                Map uploadResult =
                        cloudinary.uploader().upload(
                                file.getBytes(),
                                ObjectUtils.asMap(
                                        "folder",
                                        "cow-selling-app/cows"
                                )
                        );

                String imageUrl =
                        (String) uploadResult.get(
                                "secure_url"
                        );

                String publicId =
                        (String) uploadResult.get(
                                "public_id"
                        );

                boolean isPrimary =
                        existingImages == 0
                                && responses.isEmpty();

                CowImage cowImage =
                        CowImage.builder()
                                .imageUrl(imageUrl)
                                .publicId(publicId)
                                .primaryImage(isPrimary)
                                .cow(cow)
                                .build();

                CowImage savedImage =
                        cowImageRepository.save(cowImage);

                responses.add(
                        mapToResponse(savedImage)
                );

            } catch (IOException exception) {

                throw new RuntimeException(
                        "Failed to upload image",
                        exception
                );
            }
        }

        return responses;
    }

    @Transactional
    @Override
    public void deleteImage(
            Long imageId,
            String sellerEmail
    ) {

        CowImage image =
                cowImageRepository
                        .findById(imageId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Image not found"
                                )
                        );

        validateImageOwnership(
                image,
                sellerEmail
        );

        try {

            cloudinary.uploader().destroy(
                    image.getPublicId(),
                    ObjectUtils.emptyMap()
            );

            boolean wasPrimary =
                    image.getPrimaryImage();

            Long cowId =
                    image.getCow().getId();

            cowImageRepository.delete(image);

            if (wasPrimary) {

                List<CowImage> remainingImages =
                        cowImageRepository.findByCowIdOrderByPrimaryImageDescIdAsc(
                                cowId
                        );

                if (!remainingImages.isEmpty()) {

                    CowImage newPrimary =
                            remainingImages.get(0);

                    newPrimary.setPrimaryImage(true);

                    cowImageRepository.save(
                            newPrimary
                    );
                }
            }

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Failed to delete image",
                    exception
            );
        }
    }

    @Transactional
    @Override
    public CowImageResponse setPrimaryImage(
            Long imageId,
            String sellerEmail
    ) {

        CowImage selectedImage =
                cowImageRepository
                        .findById(imageId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Image not found"
                                )
                        );

        validateImageOwnership(
                selectedImage,
                sellerEmail
        );

        List<CowImage> images =
                cowImageRepository.findByCowId(
                        selectedImage
                                .getCow()
                                .getId()
                );

        for (CowImage image : images) {
            image.setPrimaryImage(false);
        }

        selectedImage.setPrimaryImage(true);

        cowImageRepository.saveAll(images);

        return mapToResponse(selectedImage);
    }

    private Cow getCowOwnedBySeller(
            Long cowId,
            String sellerEmail
    ) {

        Cow cow = cowRepository
                .findById(cowId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cow not found"
                        )
                );

        boolean isOwner =
                cow.getSeller()
                        .getEmail()
                        .equals(sellerEmail);

        if (!isOwner) {
            throw new ForbiddenException(
                    "You are not allowed to manage images for this cow"
            );
        }

        return cow;
    }

    private void validateImageOwnership(
            CowImage image,
            String sellerEmail
    ) {

        boolean isOwner =
                image.getCow()
                        .getSeller()
                        .getEmail()
                        .equals(sellerEmail);

        if (!isOwner) {
            throw new ForbiddenException(
                    "You are not allowed to manage this image"
            );
        }
    }

    private void validateImage(
            MultipartFile file
    ) {

        String contentType =
                file.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {

            throw new BadRequestException(
                    "Only image files are allowed"
            );
        }

        long maxFileSize =
                5 * 1024 * 1024;

        if (file.getSize() > maxFileSize) {

            throw new BadRequestException(
                    "Image size must not exceed 5 MB"
            );
        }
    }

    private CowImageResponse mapToResponse(
            CowImage image
    ) {

        return CowImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .primaryImage(
                        image.getPrimaryImage()
                )
                .build();
    }

}

