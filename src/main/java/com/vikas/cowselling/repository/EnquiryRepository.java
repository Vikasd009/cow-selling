package com.vikas.cowselling.repository;

import com.vikas.cowselling.entity.Enquiry;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.EnquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

    Page<Enquiry> findByCowSeller(
            User seller,
            Pageable pageable
    );

    Page<Enquiry> findByBuyer(
            User buyer,
            Pageable pageable
    );

    long countByCowSellerAndStatus(
            User seller,
            EnquiryStatus status
    );
}
