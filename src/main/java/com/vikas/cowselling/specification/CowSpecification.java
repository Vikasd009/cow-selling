package com.vikas.cowselling.specification;

import com.vikas.cowselling.entity.Cow;
import com.vikas.cowselling.enums.CowStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CowSpecification {

    private CowSpecification() {
    }

    public static Specification<Cow> hasStatus(
            CowStatus status
    ) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }

    public static Specification<Cow> hasBreed(
            String breed
    ) {

        return (root, query, criteriaBuilder) -> {

            if (breed == null || breed.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("breed")),
                    breed.toLowerCase()
            );
        };
    }

    public static Specification<Cow> hasCity(
            String city
    ) {

        return (root, query, criteriaBuilder) -> {

            if (city == null || city.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("city")),
                    city.toLowerCase()
            );
        };
    }

    public static Specification<Cow> hasState(
            String state
    ) {

        return (root, query, criteriaBuilder) -> {

            if (state == null || state.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("state")),
                    state.toLowerCase()
            );
        };
    }

    public static Specification<Cow> priceGreaterThanOrEqual(
            BigDecimal minPrice
    ) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice
            );
        };
    }

    public static Specification<Cow> priceLessThanOrEqual(
            BigDecimal maxPrice
    ) {

        return (root, query, criteriaBuilder) -> {

            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice
            );
        };
    }

    public static Specification<Cow> searchByNameOrBreed(
            String search
    ) {

        return (root, query, criteriaBuilder) -> {

            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String pattern =
                    "%" + search.toLowerCase() + "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("name")
                            ),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("breed")
                            ),
                            pattern
                    )
            );
        };
    }
}

