package com.vikas.cowselling.repository;

import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.CowGender;
import com.vikas.cowselling.enums.CowStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CowRepositoryTest {

    @Autowired
    private CowRepository cowRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindApprovedCows() {

        User user = new User();

        user.setName("Seller");
        user.setEmail("seller@test.com");
        user.setPassword("password");

        user = userRepository.save(user);

        Cow cow = new Cow();

        cow.setName("Lakshmi");
        cow.setBreed("Hallikar");
        cow.setAge(4);
        cow.setGender(CowGender.FEMALE);
        cow.setPrice(
                new BigDecimal("85000")
        );
        cow.setStatus(
                CowStatus.SOLD
        );
        cow.setSeller(user);

        cowRepository.save(cow);

        List<Cow> cows =
                cowRepository
                        .findByStatus(
                                CowStatus.SOLD
                        );

        assertFalse(
                cows.isEmpty()
        );

        assertEquals(
                CowStatus.SOLD,
                cows.get(0).getStatus()
        );
    }
}
