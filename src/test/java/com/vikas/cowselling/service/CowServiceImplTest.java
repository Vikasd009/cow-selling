package com.vikas.cowselling.service;

import com.vikas.cowselling.dto.request.CreateCowRequest;
import com.vikas.cowselling.dto.request.response.CowResponse;
import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.CowGender;
import com.vikas.cowselling.enums.CowStatus;
import com.vikas.cowselling.exception.ResourceNotFoundException;
import com.vikas.cowselling.repository.CowRepository;
import com.vikas.cowselling.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CowServiceImplTest {

    @Mock
    private CowRepository cowRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CowServiceImpl cowService;

    private User seller;

    private Cow cow;

    @BeforeEach
    void setUp() {

        seller = new User();

        seller.setId(1L);
        seller.setName("Seller");
        seller.setEmail("seller@example.com");

        cow = new Cow();

        cow.setId(1L);
        cow.setName("Lakshmi");
        cow.setBreed("Hallikar");
        cow.setAge(4);
        cow.setGender(CowGender.FEMALE);
        cow.setPrice(
                new BigDecimal("85000")
        );
        cow.setDescription(
                "Healthy domestic cow"
        );
        cow.setCity("Bengaluru");
        cow.setStatus(CowStatus.PENDING);
        cow.setSeller(seller);
    }

    @Test
    void shouldCreateCowSuccessfully() {

        CreateCowRequest request =
                new CreateCowRequest();

        request.setName("Lakshmi");
        request.setBreed("Hallikar");
        request.setAge(4);
        request.setGender(CowGender.FEMALE);
        request.setPrice(
                new BigDecimal("85000")
        );
        request.setDescription(
                "Healthy domestic cow"
        );
        request.setCity("Bengaluru");

        when(userRepository.findByEmail(
                "seller@example.com"
        )).thenReturn(
                Optional.of(seller)
        );

        when(cowRepository.save(
                any(Cow.class)
        )).thenReturn(cow);

        CowResponse response =
                cowService.createCow(
                        request,
                        "seller@example.com"
                );

        assertNotNull(response);

        assertEquals(
                "Lakshmi",
                response.getName()
        );

        assertEquals(
                "Hallikar",
                response.getBreed()
        );

        assertEquals(
                CowStatus.PENDING,
                response.getStatus()
        );

        verify(
                userRepository,
                times(1)
        ).findByEmail(
                "seller@example.com"
        );

        verify(
                cowRepository,
                times(1)
        ).save(
                any(Cow.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenSellerNotFound() {

        CreateCowRequest request =
                new CreateCowRequest();

        when(userRepository.findByEmail(
                "seller@example.com"
        )).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ResourceNotFoundException.class,
                () -> cowService.createCow(
                        request,
                        "seller@example.com"
                )
        );

        verify(
                cowRepository,
                never()
        ).save(any());
    }
}
