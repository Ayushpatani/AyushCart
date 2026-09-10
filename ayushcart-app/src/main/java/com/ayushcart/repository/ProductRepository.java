package com.ayushcart.repository;

import com.ayushcart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * JpaSpecificationExecutor lets us build dynamic WHERE clauses
 * (search text, category, price range) — see {@link ProductSpecifications}.
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /** Load the category in the same query to avoid one extra query per product (the "N+1" problem). */
    @Override
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    long countByActiveTrue();

    long countByActiveTrueAndStockLessThanEqual(int stock);
}
