package com.ayushcart.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(max = 150, message = "Product name must be at most 150 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        @Digits(integer = 8, fraction = 2, message = "Price can have at most 2 decimal places")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock,

        @Size(max = 500, message = "Image URL must be at most 500 characters")
        String imageUrl,

        @NotNull(message = "Category is required")
        Long categoryId,

        /** Optional; defaults to true when creating. */
        Boolean active
) {
}
