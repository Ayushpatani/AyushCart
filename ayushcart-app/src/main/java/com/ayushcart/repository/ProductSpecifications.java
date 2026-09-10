package com.ayushcart.repository;

import com.ayushcart.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Builds the product search query; every filter is optional. */
public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> filter(String search, Long categoryId,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                boolean onlyActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (onlyActive) {
                predicates.add(cb.isTrue(root.get("active")));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
