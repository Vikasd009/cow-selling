package com.vikas.cowselling.repository;

import com.vikas.cowselling.entity.CowImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CowImageRepository
        extends JpaRepository<CowImage, Long> {

    List<CowImage> findByCowId(Long cowId);

    long countByCowId(Long cowId);

    void deleteByCowId(Long cowId);

    List<CowImage> findByCowIdOrderByPrimaryImageDescIdAsc(Long cowId);
}

