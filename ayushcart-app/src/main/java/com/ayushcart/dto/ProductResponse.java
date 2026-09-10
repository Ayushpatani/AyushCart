package com.ayushcart.dto;

import com.ayushcart.entity.Product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock,
        String imageUrl,
        Long categoryId,
        String categoryName,
        boolean active,
        Instant createdAt
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getStock(), p.getImageUrl(), p.getCategory().getId(), p.getCategory().getName(),
                p.isActive(), p.getCreatedAt());
    }
}
