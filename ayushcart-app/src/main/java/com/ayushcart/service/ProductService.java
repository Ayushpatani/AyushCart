package com.ayushcart.service;

import com.ayushcart.dto.PageResponse;
import com.ayushcart.dto.ProductRequest;
import com.ayushcart.dto.ProductResponse;
import com.ayushcart.entity.Product;
import com.ayushcart.exception.BadRequestException;
import com.ayushcart.exception.ResourceNotFoundException;
import com.ayushcart.repository.ProductRepository;
import com.ayushcart.repository.ProductSpecifications;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    /**
     * @param onlyActive true for the shop (customers), false for the admin panel.
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(String search, Long categoryId, BigDecimal minPrice,
                                                BigDecimal maxPrice, boolean onlyActive, Pageable pageable) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("Minimum price cannot be greater than maximum price");
        }
        var spec = ProductSpecifications.filter(search, categoryId, minPrice, maxPrice, onlyActive);
        return PageResponse.from(productRepository.findAll(spec, pageable).map(ProductResponse::from));
    }

    /** Shop view: inactive products look like they don't exist. */
    @Transactional(readOnly = true)
    public ProductResponse getActive(Long id) {
        Product product = getEntity(id);
        if (!product.isActive()) {
            throw new ResourceNotFoundException("Product " + id + " not found");
        }
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getForAdmin(Long id) {
        return ProductResponse.from(getEntity(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest r) {
        Product product = new Product(r.name().trim(), r.description(), r.price(), r.stock(),
                blankToNull(r.imageUrl()), categoryService.getEntity(r.categoryId()));
        if (r.active() != null) {
            product.setActive(r.active());
        }
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest r) {
        Product product = getEntity(id);
        product.setName(r.name().trim());
        product.setDescription(r.description());
        product.setPrice(r.price());
        product.setStock(r.stock());
        product.setImageUrl(blankToNull(r.imageUrl()));
        product.setCategory(categoryService.getEntity(r.categoryId()));
        if (r.active() != null) {
            product.setActive(r.active());
        }
        return ProductResponse.from(product);
    }

    /**
     * Soft delete: the product is hidden from the shop but kept in the database,
     * because past orders still point to it. Re-enable it by updating with active=true.
     */
    @Transactional
    public void deactivate(Long id) {
        getEntity(id).setActive(false);
    }

    Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " not found"));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
