package com.vikas.cowselling.repository;

import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.entity.User;
import com.vikas.cowselling.enums.CowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CowRepository extends JpaRepository<Cow, Long>, JpaSpecificationExecutor<Cow> {

    List<Cow> findBySeller(User seller);

    List<Cow> findByStatus(CowStatus status);

    Page<Cow> findByStatus(CowStatus status, Pageable pageable);

    long countByStatus(CowStatus status);
}
