package com.ayushcart.controller;

import com.ayushcart.dto.CategoryResponse;
import com.ayushcart.dto.PageResponse;
import com.ayushcart.dto.ProductResponse;
import com.ayushcart.service.CategoryService;
import com.ayushcart.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/** Public, read-only endpoints for browsing the shop. No login needed. */
@RestController
@RequestMapping("/api")
public class CatalogController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public CatalogController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * Example: GET /api/products?search=shirt&categoryId=2&minPrice=100&maxPrice=999&page=0&size=12&sort=price,asc
     */
    @GetMapping("/products")
    public PageResponse<ProductResponse> products(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.search(search, categoryId, minPrice, maxPrice, true, pageable);
    }

    @GetMapping("/products/{id}")
    public ProductResponse product(@PathVariable Long id) {
        return productService.getActive(id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return categoryService.findAll();
    }
}
